package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Entities.Account;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class AccountServiceImplTest {

    @Mock
    AccountDao accountDao;

    @Mock
    AccountDaoWithCRUD accountDaoWithCRUD;

    private AccountService accountService = new AccountServiceImpl(accountDao,accountDaoWithCRUD);

    @Test
    public void checkingIfMethodChangingNameCorrectly() {

        Account accountToChange = new Account("2222","PLN","Robert", BigDecimal.valueOf(100));
        String name = "Janusz";
        Account accountResult = new Account("2222","PLN","Januszek", BigDecimal.valueOf(100));

        Assert.assertEquals(accountResult,accountService.setNewNameToAccount(accountToChange,name));
    }
}