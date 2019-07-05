package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private AccountDao accountDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao,AccountDaoWithCRUD accountDaoWithCRUD) {
        this.accountDao = accountDao;
        this.accountDaoWithCRUD = accountDaoWithCRUD;
    }

    @Override
    public Account getAccountByIdAccount(int accountId) {

        return accountDaoWithCRUD.getAccountsById(accountId);
    }

    @Override
    public Account getAccountByNumberAccount(String numberAccount) {

        return accountDao.getAccountByNumberOfAccount(numberAccount);
    }

    @Override
    public Account changeNameAccountByIdAccount(int accountId, Account account) {
        Account accountToChange = accountDaoWithCRUD.getAccountsById(accountId);
        
        accountToChange.setName(account.getName());
        
        accountDaoWithCRUD.save(accountToChange);
        
        return accountToChange;
    }
}
