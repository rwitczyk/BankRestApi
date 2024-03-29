package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Data.UserMagda;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Exceptions.account.AccountByIdAccountNotExistException;
import com.restApi.RestApi.Exceptions.account.AccountByNumberAccountNotExistException;
import com.restApi.RestApi.Exceptions.account.AddAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Currency;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao, AccountDaoWithCRUD accountDaoWithCRUD) {
        this.accountDao = accountDao;
        this.accountDaoWithCRUD = accountDaoWithCRUD;
    }

    @Override
    public Account getAccountByIdAccount(int accountId) {
        Account account = accountDaoWithCRUD.getAccountById(accountId);
        if (account == null) {
            throw new AccountByIdAccountNotExistException("Konto o podanym id:" + accountId + " nie istnieje");
        }

        return account;
    }

    @Override
    public Account getAccountByNumberAccount(String numberAccount) {
        Account account = accountDao.getAccountByNumberOfAccount(numberAccount);
        if (account == null) {
            throw new AccountByNumberAccountNotExistException("Konto o podanym numerze:" + numberAccount + " nie istnieje");
        }

        return account;
    }

    @Override
    public Account changeNameAccountByIdAccount(int accountId, String newName) {
        Account accountToChange = accountDaoWithCRUD.getAccountById(accountId);

        if (accountToChange != null) {
            accountToChange.setName(newName);
            accountDaoWithCRUD.save(accountToChange);
            return accountToChange;
        } else {
            throw new AccountByIdAccountNotExistException("Konto o podanym id:" + accountId + " nie istnieje");
        }
    }

    @Override
    public void deleteAccountByIdAccount(int accountId) {
        if (accountDaoWithCRUD.getAccountById(accountId) != null) {
            accountDaoWithCRUD.deleteAccountsById(accountId);
        } else {
            throw new AccountByIdAccountNotExistException("Konto o podanym id:" + accountId + " nie istnieje");
        }
    }

    @Override
    public void addAccount(Account account) {
        try {
            Currency.getInstance(account.getCurrency());
            accountDao.addAccount(account);
        } catch (IllegalArgumentException e) {
            throw new AddAccountException("Nie mozna dodac konta - sprawdz poprawnosc danych");
        }
    }

    @Override
    public UserMagda[] getUsersFromMagda() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://restapi97.herokuapp.com/api/accounts";
        return restTemplate.getForObject(apiUrl, UserMagda[].class);
    }
}
