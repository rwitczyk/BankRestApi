package com.restApi.RestApi.Services;

import com.restApi.RestApi.Entities.Account;

public interface AccountService {

    Account getAccountByIdAccount(int accountId);

    Account getAccountByNumberAccount(String numberAccount);
}
