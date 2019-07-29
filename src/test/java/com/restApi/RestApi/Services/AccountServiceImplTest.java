package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Exceptions.account.AccountByIdAccountNotExistException;
import com.restApi.RestApi.Exceptions.account.AccountByNumberAccountNotExistException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

public class AccountServiceImplTest {

    private AccountDao accountDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;
    private AccountService accountService;

    @Before
    public void setUp() throws Exception {
        accountDao = Mockito.mock(AccountDao.class);
        accountDaoWithCRUD = Mockito.mock(AccountDaoWithCRUD.class);
        accountService = new AccountServiceImpl(accountDao, accountDaoWithCRUD);
    }

    @Test
    public void shouldChangeNameOfAccountCorrectly() {
        // given
        Account accountToChange = new Account("2222", "PLN", "Robert", BigDecimal.valueOf(100));
        String name = "Janusz";
        Account accountResult = new Account("2222", "PLN", "Janusz", BigDecimal.valueOf(100));

        // when
        Mockito.when(accountDaoWithCRUD.getAccountById(Mockito.anyInt())).thenReturn(accountToChange);
        accountService.changeNameAccountByIdAccount(3, name);

        // then
        Assert.assertEquals(accountResult, accountToChange);
    }

    @Test(expected = AccountByNumberAccountNotExistException.class)
    public void shouldThrowExceptionIfAccountNotExistByNumberAccount() {
        accountService.getAccountByNumberAccount("1111");
    }

    @Test(expected = AccountByIdAccountNotExistException.class)
    public void shouldThrowExceptionIfAccountNotExistByIdAccount() {
        accountService.getAccountByIdAccount(3);
    }

}
