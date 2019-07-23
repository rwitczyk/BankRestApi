package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Data.Currency;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Entities.TransferDto;
import com.restApi.RestApi.Exceptions.account.ReturnMoneyToSourceAccountException;
import com.restApi.RestApi.Exceptions.transfer.NoTransfersException;
import com.restApi.RestApi.Exceptions.transfer.ReturnTransfersByIdAccountException;
import com.restApi.RestApi.Exceptions.transfer.SaveNewTransferException;
import com.restApi.RestApi.StatusTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private TransferDao transferDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;
    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);
    private Account accountFrom;
    private Account accountTo;
    private BigDecimal transferBalanceBefore;
    private BigDecimal transferBalanceAfter;

    @Autowired
    public TransferServiceImpl(TransferDao transferDao, AccountDaoWithCRUD accountDaoWithCRUD) {
        this.transferDao = transferDao;
        this.accountDaoWithCRUD = accountDaoWithCRUD;
    }

    @Override
    public boolean createTransfer(TransferDto transferDto) {
        accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transferDto.getFromNumberAccount());
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transferDto.getToNumberAccount());
        if (isEnoughMoneyOnSourceAccount(transferDto)) {
            if (accountTo != null) { // czy konto docelowe istnieje
                saveNewTransfer(transferDto);
                subtractMoneyFromSourceAccount(transferDto);
                return true;
            }
        } else {
            log.error("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
        }
        throw new SaveNewTransferException("Brak środków na koncie lub konto nie istnieje");
    }

    private boolean isEnoughMoneyOnSourceAccount(TransferDto transferDto) {
        return accountFrom.getBalance().subtract(transferDto.getBalanceBeforeChangeCurrency()).compareTo(BigDecimal.valueOf(0)) > 0;
    }

    private void subtractMoneyFromSourceAccount(TransferDto transferData) {
        accountFrom.setBalance(accountFrom.getBalance().subtract(transferData.getBalanceBeforeChangeCurrency()));
        accountDaoWithCRUD.save(accountFrom);
    }

    private void saveNewTransfer(TransferDto transferData) {
        transferData.setCreateTransferDate(String.valueOf(LocalDateTime.now()));
        transferData.setStatus(StatusTransfer.CREATED.getValue());
        transferBalanceBefore = transferData.getBalanceBeforeChangeCurrency();
        transferData.setCurrencyDestinationAccount(accountTo.getCurrency());
        convertCurrency();
        transferData.setBalanceAfterChangeCurrency(transferBalanceAfter);

        // dto to Transfer
        Transfer transferToSave = convertTransferDtoToTransfer(transferData);
        transferDao.save(transferToSave);
    }

    public void convertCurrency() {
        RestTemplate restTemplate = new RestTemplate();
        String currencyApiUrl = "https://api.exchangeratesapi.io/latest?base=";
        String currencyFrom = accountFrom.getCurrency();
        String currencyTo = accountTo.getCurrency();

        if (!currencyFrom.equals(currencyTo)) { // czy waluty sa rozne od siebie

            Currency currency = restTemplate.getForObject(currencyApiUrl + currencyFrom, Currency.class);

            BigDecimal multiplyCurrency = Objects.requireNonNull(currency, "Currency nie moze byc null!").getRates().get(currencyTo);
            log.info("Mnoznik waluty: " + multiplyCurrency);

            transferBalanceAfter = transferBalanceBefore.multiply(multiplyCurrency);
            transferBalanceAfter = transferBalanceAfter.setScale(2, RoundingMode.CEILING);
        } else {
            transferBalanceAfter = transferBalanceBefore;
        }
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

                    addMoneyToDestinationAccount(transfer);
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

    private void addMoneyToDestinationAccount(Transfer transfer) {
        transferBalanceAfter = transfer.getBalanceAfterChangeCurrency();
        accountTo = accountDaoWithCRUD.getAccountById(transfer.getToAccount().getId());
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
        List<TransferDto> result = convertTransfersToTransfersDto(transfers);

        if (transfers.size() == 0) {
            throw new ReturnTransfersByIdAccountException("Brak transferow z konta o id: " + numberAccount);
        }
        return result;
    }

    @Override
    public List<TransferDto> getTransfersByToNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByToAccountNumberAccount(numberAccount);
        List<TransferDto> result = convertTransfersToTransfersDto(transfers);

        if (transfers.size() == 0) {
            throw new ReturnTransfersByIdAccountException("Brak transferow z konta o id: " + numberAccount);
        }
        return result;
    }

    @Override
    public void cancelTransfer(Transfer transfer) {
        if (!returnMoneyToSourceAccount(transfer)) {
            throw new ReturnMoneyToSourceAccountException("Zwrot pieniędzy na konto źródłowe nie powiódł się");
        }
    }

    private List<TransferDto> convertTransfersToTransfersDto(List<Transfer> transfers) {
        List<TransferDto> transferDtos = new ArrayList<>();
        for (Transfer transfer: transfers
             ) {
            TransferDto transferDto = new TransferDto();
            transferDto.setToNumberAccount(transfer.getToAccount().getNumberAccount());
            transferDto.setFromNumberAccount(transfer.getFromAccount().getNumberAccount());
            transferDto.setCreateTransferDate(transfer.getCreateTransferDate());
            transferDto.setExecuteTransferDate(transfer.getExecuteTransferDate());
            transferDto.setStatus(transfer.getStatus());
            transferDto.setCurrencyDestinationAccount(transfer.getCurrencyDestinationAccount());
            transferDto.setBalanceBeforeChangeCurrency(transfer.getBalanceBeforeChangeCurrency());
            transferDto.setBalanceAfterChangeCurrency(transfer.getBalanceAfterChangeCurrency());
            transferDtos.add(transferDto);
        }
        return transferDtos;
    }

    private Transfer convertTransferDtoToTransfer(TransferDto transferData) {
        Transfer transferToSave = new Transfer();
        transferToSave.setFromAccount(accountFrom);
        transferToSave.setToAccount(accountTo);
        transferToSave.setStatus(transferData.getStatus());
        transferToSave.setCurrencyDestinationAccount(transferData.getCurrencyDestinationAccount());
        transferToSave.setBalanceBeforeChangeCurrency(transferData.getBalanceBeforeChangeCurrency());
        transferToSave.setBalanceAfterChangeCurrency(transferData.getBalanceAfterChangeCurrency());
        transferToSave.setCreateTransferDate(transferData.getCreateTransferDate());
        return transferToSave;
    }
}
