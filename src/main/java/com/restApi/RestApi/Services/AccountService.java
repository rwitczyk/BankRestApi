package com.restApi.RestApi.Services;

import com.restApi.RestApi.Data.UserMagda;
import com.restApi.RestApi.Entities.Account;

public interface AccountService {

    Account getAccountByIdAccount(int accountId);

    Account getAccountByNumberAccount(String numberAccount);

    Account changeNameAccountByIdAccount(int accountId, String newName);

    void deleteAccountByIdAccount(int accountId);

    void addAccount(Account account);

    UserMagda[] getUsersFromMagda();
}
