package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Account;

import java.util.List;

public interface AccountDao {

    Account addAccount(Account account);

    List<Account> getAllAccounts();

    Account getAccountByNumberOfAccount(String accountNumber);
}
