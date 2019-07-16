package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Data.Currency;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
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

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private TransferDao transferDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;

    @Autowired
    public TransferServiceImpl(TransferDao transferDao,AccountDaoWithCRUD accountDaoWithCRUD) {
        this.transferDao = transferDao;
        this.accountDaoWithCRUD = accountDaoWithCRUD;
    }

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private String currencyFrom;
    private String currencyTo;
    private Account accountFrom;
    private Account accountTo;
    private BigDecimal transferBalanceBefore;
    private BigDecimal transferBalanceAfter;

    @Override
    public boolean saveNewTransfer(Transfer transferData) {
        accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transferData.getFromNumberAccount());
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transferData.getToNumberAccount());
        if(accountFrom.getBalance().subtract(transferData.getBalance()).compareTo(BigDecimal.valueOf(0)) == 1) //walidacja czy masz >0 pieniedzy na koncie
        {
            if(accountTo != null) { // czy konto docelowe istnieje
              transferData.setCreateTransferDate(String.valueOf(LocalDateTime.now()));
              transferData.setStatus(String.valueOf(StatusTransfer.CREATED));
              transferBalanceBefore = transferData.getBalance();
              transferData.setCurrency(accountTo.getCurrency());

              accountFrom.setBalance(accountFrom.getBalance().subtract(transferData.getBalance())); //substract money from source account
                convertCurrency();
                transferData.setBalance(transferBalanceAfter);

                accountDaoWithCRUD.save(accountFrom);
                transferDao.save(transferData);

                return true;
            }
        }
        else
        {
            log.error("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
            return false;
        }

        return false;
    }

    public void convertCurrency() {
        RestTemplate restTemplate = new RestTemplate();
        String currencyApiUrl = "https://api.exchangeratesapi.io/latest?base=";
        currencyFrom = accountFrom.getCurrency();
        currencyTo = accountTo.getCurrency();

        if(!currencyFrom.equals(currencyTo)) { // czy waluty sa rozne od siebie

            Currency currency = restTemplate.getForObject(currencyApiUrl + currencyFrom, Currency.class);

            double multiplyCurrency = currency.getRates().get(currencyTo);
            log.info("MNOZNIK: " + multiplyCurrency);

            transferBalanceAfter = transferBalanceBefore.multiply(BigDecimal.valueOf(multiplyCurrency));
            transferBalanceAfter = transferBalanceAfter.setScale(2, RoundingMode.CEILING);
        }
        else {
            transferBalanceAfter = transferBalanceBefore;
        }
    }

    public void finishTransfers()
    {
        log.info("Finishing transfers...");
        Iterable<Transfer> transfers = transferDao.findAll();
        if(transfers!=null) {
            transfers.forEach(transfer -> {
                if (transfer.getStatus().equals(String.valueOf(StatusTransfer.CREATED))) {
                    transfer.setStatus(String.valueOf(StatusTransfer.DONE));
                    transfer.setExecuteTransferDate(String.valueOf(LocalDateTime.now()));
                    transferDao.save(transfer);

                    addMoneyToDestinationAccount(transfer);
                }
            });
        }
    }


    private void addMoneyToDestinationAccount(Transfer transfer) {
        transferBalanceAfter = transfer.getBalance();
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getToNumberAccount());
        accountTo.setBalance(accountTo.getBalance().add(transferBalanceAfter));
        accountDaoWithCRUD.save(accountTo);
    }

    @Override
    public Iterable<Transfer> getAllTransfers() {
        return transferDao.findAll();
    }

    @Override
    public List<Transfer> getTransfersByFromNumberAccount(String numberAccount) {
        return transferDao.getTransfersByFromNumberAccount(numberAccount);
    }

    @Override
    public List<Transfer> getTransfersByToNumberAccount(String numberAccount) {
        return transferDao.getTransfersByToNumberAccount(numberAccount);
    }
}
