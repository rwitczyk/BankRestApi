package com.restApi.RestApi.Services;

import com.restApi.RestApi.Converters.ClassConverter;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.ExternalTransferDao;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Data.Currency;
import com.restApi.RestApi.Entities.*;
import com.restApi.RestApi.Exceptions.account.ReturnMoneyToSourceAccountException;
import com.restApi.RestApi.Exceptions.transfer.ExternalTransferException;
import com.restApi.RestApi.Exceptions.transfer.NoTransfersException;
import com.restApi.RestApi.Exceptions.transfer.ReturnTransfersByIdAccountException;
import com.restApi.RestApi.Exceptions.transfer.SaveNewTransferException;
import com.restApi.RestApi.StatusTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private TransferDao transferDao;
    private ExternalTransferDao externalTransferDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;
    private JavaMailSender javaMailSender;
    private ClassConverter classConverter = new ClassConverter();
    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Autowired
    public TransferServiceImpl(TransferDao transferDao, AccountDaoWithCRUD accountDaoWithCRUD,
                               ExternalTransferDao externalTransferDao, JavaMailSender javaMailSender) {
        this.transferDao = transferDao;
        this.accountDaoWithCRUD = accountDaoWithCRUD;
        this.externalTransferDao = externalTransferDao;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void chooseTransfer(TransferDto transferDto) {
        Account accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transferDto.getFromNumberAccount());
        transferDto.setCurrencyFromAccount(accountFrom.getCurrency());

        if (transferDto.getTransferToBank() == 0) { // wewnetrzny
            createTransfer(transferDto, accountFrom);
        } else if (transferDto.getTransferToBank() == 1) { // zewnetrzny
            createExteriorTransfer(transferDto, accountFrom);
        }
    }

    @Override
    public boolean createTransfer(TransferDto transferDto, Account accountFrom) {
        Account accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transferDto.getToNumberAccount());
        transferDto.setCurrencyFromAccount(accountFrom.getCurrency());
        if (isEnoughMoneyOnSourceAccount(transferDto, accountFrom)) {
            if (accountTo != null) {
                saveNewTransfer(transferDto, accountFrom, accountTo);
                subtractMoneyFromSourceAccount(transferDto, accountFrom);
                return true;
            }
        } else {
            log.error("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
        }
        throw new SaveNewTransferException("Brak środków na koncie lub konto nie istnieje");
    }

    @Override
    public void createExteriorTransfer(TransferDto transferDto, Account accountFrom) {
        if (isEnoughMoneyOnSourceAccount(transferDto, accountFrom)) {
            ExternalTransferDto externalTransferDto = classConverter.convertTransferDtoToExternalTransferDto(transferDto);
            RestTemplate rest = new RestTemplate();
            final String urlApiArek = "https://comarch.herokuapp.com/";
            ResponseEntity<String> postTransfer = rest.postForEntity(urlApiArek + "transfer/external-transfer", externalTransferDto, String.class);

            System.out.println("WYSLANO PRZELEW DO ARKA");
            if (postTransfer.getStatusCode() == HttpStatus.OK) {
                if(transferDto.getEmail().length() > 2)
                {
                    sendEmail(transferDto);
                }
                System.out.println("STATUS OK");
                subtractMoneyFromSourceAccount(transferDto, accountFrom);
                ExternalTransfer externalTransfer = classConverter.convertTransferDtoToExternalTransfer(transferDto, accountFrom);
                externalTransferDao.save(externalTransfer);
            } else {
                throw new ExternalTransferException("Transfer zewnetrzny nie powiódł się!");
            }
        } else {
            throw new SaveNewTransferException("Brak środków na koncie lub konto nie istnieje");
        }
    }

    void sendEmail(TransferDto transferDto) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(transferDto.getEmail());
        msg.setSubject("Wykonano przelew!");
        msg.setText("Bank Roblox \n" +
                "Zlecono przelew z konta: " + transferDto.getFromNumberAccount() + " na kwotę: " +
                transferDto.getBalanceAfterChangeCurrency() + transferDto.getCurrencyDestinationAccount() + " na numer" +
                "konta: " + transferDto.getToNumberAccount());
        javaMailSender.send(msg);

    }

    public boolean isEnoughMoneyOnSourceAccount(TransferDto transferDto, Account accountFrom) {
        return accountFrom.getBalance().subtract(transferDto.getBalanceBeforeChangeCurrency()).compareTo(BigDecimal.valueOf(0)) > 0;
    }

    private void subtractMoneyFromSourceAccount(TransferDto transferData, Account accountFrom) {
        accountFrom.setBalance(accountFrom.getBalance().subtract(transferData.getBalanceBeforeChangeCurrency()));
        accountDaoWithCRUD.save(accountFrom);
    }

    private void saveNewTransfer(TransferDto transferData, Account accountFrom, Account accountTo) {
        transferData.setCreateTransferDate(String.valueOf(LocalDateTime.now()));
        transferData.setStatus(StatusTransfer.CREATED.getValue());
        transferData.setCurrencyDestinationAccount(accountTo.getCurrency());

        BigDecimal multiplyCurrency = convertCurrency(accountFrom.getCurrency(), accountTo.getCurrency());
        BigDecimal transferBalanceAfter = transferData.getBalanceBeforeChangeCurrency().multiply(multiplyCurrency);
        transferBalanceAfter = transferBalanceAfter.setScale(2, RoundingMode.CEILING);
        transferData.setBalanceAfterChangeCurrency(transferBalanceAfter);

        Transfer transferToSave = classConverter.convertTransferDtoToTransfer(transferData, accountFrom, accountTo);
        transferDao.save(transferToSave);
    }

    public BigDecimal convertCurrency(String currencyFrom, String currencyTo) {
        BigDecimal multiplyCurrency;
        RestTemplate restTemplate = new RestTemplate();
        String currencyApiUrl = "https://api.exchangeratesapi.io/latest?base=";

        if (!currencyFrom.equals(currencyTo)) { // czy waluty sa rozne od siebie
            Currency currency = restTemplate.getForObject(currencyApiUrl + currencyFrom, Currency.class);
            multiplyCurrency = Objects.requireNonNull(currency, "Currency nie moze byc null!").getRates().get(currencyTo);
            log.info("Mnoznik waluty: " + multiplyCurrency);
        } else {
            return BigDecimal.valueOf(1);
        }
        return multiplyCurrency;
    }

    public void finishTransfers() {
        log.info("Księgowanie przelewów...");
        Iterable<Transfer> transfers = transferDao.getTransfersByStatus(StatusTransfer.CREATED.getValue());
        if (transfers != null) {
            transfers.forEach(transfer -> {
                Account accountDestination = accountDaoWithCRUD.getAccountById(transfer.getToAccount().getId());
                if (accountDestination != null) {
                    transfer.setStatus(StatusTransfer.DONE.getValue());
                    transfer.setExecuteTransferDate(String.valueOf(LocalDateTime.now()));
                    transferDao.save(transfer);

                    addMoneyToDestinationAccount(transfer, accountDestination);
                } else {
                    returnMoneyToSourceAccount(transfer);
                }
            });
        }
    }

    private boolean returnMoneyToSourceAccount(Transfer transfer) {
        if (transfer.getStatus().equals(StatusTransfer.CREATED.getValue())) {
            Account accountSource = accountDaoWithCRUD.getAccountById(transfer.getFromAccount().getId());
            if (accountSource != null) {
                accountSource.setBalance(accountSource.getBalance().add(transfer.getBalanceBeforeChangeCurrency()));
                accountDaoWithCRUD.save(accountSource);
                log.info("Zwracam pieniadze na konto zrodlowe");

                transfer.setStatus(StatusTransfer.CANCELLED.getValue());
                transfer.setExecuteTransferDate("-");
                transferDao.save(transfer);

                return true;
            }
        }
        return false;
    }

    private void addMoneyToDestinationAccount(Transfer transfer, Account accountTo) {
        BigDecimal transferBalanceAfter = transfer.getBalanceAfterChangeCurrency();
        accountTo.setBalance(accountTo.getBalance().add(transferBalanceAfter));
        accountDaoWithCRUD.save(accountTo);
    }

    @Override
    public Iterable<Transfer> getAllTransfers() {
        if (transferDao.findAll() != null) {
            return transferDao.findAll();
        }
        throw new NoTransfersException("Brak transferów do pobrania");
    }

    @Override
    public List<TransferDto> getTransfersByFromNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByFromAccountNumberAccount(numberAccount);
        List<TransferDto> result = classConverter.convertTransfersToTransfersDto(transfers);

        if (transfers.size() == 0) {
            throw new ReturnTransfersByIdAccountException("Brak transferow z konta o id: " + numberAccount);
        }
        return result;
    }

    @Override
    public List<TransferDto> getTransfersByToNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByToAccountNumberAccount(numberAccount);
        List<TransferDto> result = classConverter.convertTransfersToTransfersDto(transfers);

        if (transfers.size() == 0) {
            throw new ReturnTransfersByIdAccountException("Brak transferow z konta o id: " + numberAccount);
        }
        return result;
    }

    @Override
    public void cancelTransfer(TransferDto transferDto) {
        Transfer transfer = transferDao.getTransferById(transferDto.getId());

        if (!returnMoneyToSourceAccount(transfer)) {
            throw new ReturnMoneyToSourceAccountException("Zwrot pieniędzy na konto źródłowe nie powiódł się");
        }
    }
}
