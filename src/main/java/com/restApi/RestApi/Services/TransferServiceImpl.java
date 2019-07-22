package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Data.Currency;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Exceptions.account.ReturnMoneyToSourceAccountException;
import com.restApi.RestApi.Exceptions.account.ReturnTransferByNumberAccountException;
import com.restApi.RestApi.Exceptions.transfer.NoTransfersException;
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
    public boolean createTransfer(Transfer transferData) {
        accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transferData.getFromNumberAccount());
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transferData.getToNumberAccount());
        if (isEnoughMoneyOnSourceAccount(transferData)) {
            if (accountTo != null) { // czy konto docelowe istnieje
                saveNewTransfer(transferData);
                subtractMoneyFromSourceAccount(transferData);
                return true;
            }
        } else {
            log.error("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
        }
        throw new SaveNewTransferException("Brak środków na koncie lub konto nie istnieje");
    }

    private boolean isEnoughMoneyOnSourceAccount(Transfer transferData) {
        return accountFrom.getBalance().subtract(transferData.getBalanceBeforeChangeCurrency()).compareTo(BigDecimal.valueOf(0)) > 0;
    }

    private void subtractMoneyFromSourceAccount(Transfer transferData) {
        accountFrom.setBalance(accountFrom.getBalance().subtract(transferData.getBalanceBeforeChangeCurrency()));
        accountDaoWithCRUD.save(accountFrom);
    }

    private void saveNewTransfer(Transfer transferData) {
        transferData.setCreateTransferDate(String.valueOf(LocalDateTime.now()));
        transferData.setStatus(StatusTransfer.CREATED.getValue());
        transferBalanceBefore = transferData.getBalanceBeforeChangeCurrency();
        transferData.setCurrencyDestinationAccount(accountTo.getCurrency());
        convertCurrency();
        transferData.setBalanceAfterChangeCurrency(transferBalanceAfter);
        transferDao.save(transferData);
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
                Account accountDestination = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getToNumberAccount());
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
            Account accountSource = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getFromNumberAccount());
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
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getToNumberAccount());
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
    public List<Transfer> getTransfersByFromNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByFromNumberAccount(numberAccount);
        if (transfers.size() == 0) {
            throw new ReturnTransferByNumberAccountException("Brak transferow z tego konta: " + numberAccount);
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByToNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByToNumberAccount(numberAccount);
        if (transfers.size() == 0) {
            throw new ReturnTransferByNumberAccountException("Brak transferow z tego konta: " + numberAccount);
        }
        return transfers;
    }

    @Override
    public void cancelTransfer(Transfer transfer) {
        if (!returnMoneyToSourceAccount(transfer)) {
            throw new ReturnMoneyToSourceAccountException("Zwrot pieniędzy na konto źródłowe nie powiódł się");
        }
    }
}
