package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Data.Currency;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    @Autowired
    TransferDao transferDao;

    @Autowired
    AccountDaoWithCRUD accountDaoWithCRUD;

    private String currencyFrom;
    private String currencyTo;
    private Account accountFrom;
    private Account accountTo;
    BigDecimal transferBalanceBefore;
    BigDecimal transferBalanceAfter;

    @Override
    public Transfer saveNewTransfer(Transfer transferData) {
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
            System.out.println("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
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
        System.out.println("MNOZNIK: " + multiplyCurrency);

        transferBalanceAfter = transferBalanceBefore.multiply(BigDecimal.valueOf(multiplyCurrency));
        transferBalanceAfter = transferBalanceAfter.setScale(2, RoundingMode.CEILING);
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
