package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Exceptions.account.AccountByIdAccountNotExistException;
import com.restApi.RestApi.Exceptions.account.AccountByNumberAccountNotExistException;
import com.restApi.RestApi.Exceptions.account.AddAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Account account = accountDaoWithCRUD.getAccountsById(accountId);
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
    public Account changeNameAccountByIdAccount(int accountId, Account account) {
        Account accountToChange = accountDaoWithCRUD.getAccountsById(accountId);

        if (accountToChange != null) {
            setNewNameToAccount(accountToChange, account.getName());
            accountDaoWithCRUD.save(accountToChange);
            return accountToChange;
        } else {
            throw new AccountByIdAccountNotExistException("Konto o podanym id:" + accountId + " nie istnieje");
        }
    }

    @Override
    public Account setNewNameToAccount(Account accountToChange, String name) {
        accountToChange.setName(name);

        return accountToChange;
    }

    @Override
    public void deleteAccountByIdAccount(int accountId) {
        if (accountDaoWithCRUD.getAccountsById(accountId) != null) {
            accountDaoWithCRUD.deleteAccountsById(accountId);
        } else {
            throw new AccountByIdAccountNotExistException("Konto o podanym id:" + accountId + " nie istnieje");
        }
    }

    @Override
    public void addAccount(Account account) {
        try{
            Currency.getInstance(account.getCurrency());
            accountDao.addAccount(account);
        }
        catch (IllegalArgumentException e) {
            throw new AddAccountException("Nie mozna dodac konta - sprawdz poprawnosc danych");
        }

    }
}
