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

    @Autowired
    TransferDao transferDao;

    @Autowired
    AccountDaoWithCRUD accountDaoWithCRUD;

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private String currencyFrom;
    private String currencyTo;
    private Account accountFrom;
    private Account accountTo;
    BigDecimal transferBalanceBefore;
    BigDecimal transferBalanceAfter;

    @Override
    public Transfer saveNewTransfer(Transfer transferData) {
        transferData.setCreateTransferDate(String.valueOf(LocalDateTime.now()));
        transferData.setStatus(String.valueOf(StatusTransfer.CREATED));

        Transfer transfer = transferDao.save(transferData);
        transferBalanceBefore = transfer.getBalance();

        accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getFromNumberAccount());
        accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getToNumberAccount());

        if(accountFrom.getBalance().subtract(transfer.getBalance()).compareTo(BigDecimal.valueOf(0)) == 1) //walidacja czy masz >0 pieniedzy na koncie
        {
            accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getBalance()));
            convertCurrency();
            accountTo.setBalance(accountTo.getBalance().add(transferBalanceAfter));

            accountDaoWithCRUD.save(accountTo);
            accountDaoWithCRUD.save(accountFrom);
        }
        else
        {
            log.error("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
        }

        return transfer;
    }

    private void convertCurrency() {
        RestTemplate restTemplate = new RestTemplate();
        String currencyApiUrl = "https://api.exchangeratesapi.io/latest?base=";
        currencyFrom = accountFrom.getCurrency();
        currencyTo = accountTo.getCurrency();

        Currency currency = restTemplate.getForObject(currencyApiUrl + currencyFrom, Currency.class);

        double multiplyCurrency = currency.getRates().get(currencyTo);
        log.info("MNOZNIK: " + multiplyCurrency);

        transferBalanceAfter = transferBalanceBefore.multiply(BigDecimal.valueOf(multiplyCurrency));
        transferBalanceAfter = transferBalanceAfter.setScale(2, RoundingMode.CEILING);
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
                }
            });
        }
    }

    @Override
    public Iterable<Transfer> getAllTransfers() {
        Iterable<Transfer> transfers = transferDao.findAll();

        return transfers;
    }

    @Override
    public List<Transfer> getTranfersByNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByFromNumberAccount(numberAccount);

        return transfers;
    }
}
