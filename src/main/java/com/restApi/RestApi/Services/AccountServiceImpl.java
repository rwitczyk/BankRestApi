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

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountDaoWithCRUD accountDaoWithCRUD;

    @Override
    public Account getAccountByIdAccount(int accountId) {
        Account account = accountDaoWithCRUD.getAccountsById(accountId);

        return account;
    }

    @Override
    public Account getAccountByNumberAccount(String numberAccount) {
        Account account = accountDao.getAccountByNumberOfAccount(numberAccount);

        return account;
    }

    @Override
    public Account changeNameAccountByIdAccount(int accountId, Account account) {
        Account accountToChange = accountDaoWithCRUD.getAccountsById(accountId);
        
        accountToChange.setName(account.getName());
        
        accountDaoWithCRUD.save(accountToChange);
        
        return accountToChange;
    }
}
