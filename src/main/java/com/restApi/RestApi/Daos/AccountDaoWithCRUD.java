package com.restApi.RestApi.Daos;

import com.restApi.RestApi.Entities.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountDaoWithCRUD extends CrudRepository<Account,Integer> {

    Account getAccountsById(int id);

    Account getAccountByNumberAccount(String numberAccount);

    void deleteAccountsById(int id);
}
