package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.ExternalTransferDao;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.TransferDto;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

public class TransferServiceImplTest {
    @Mock
    TransferDao transferDao;
    @Mock
    AccountDaoWithCRUD accountDaoWithCRUD;
    @Mock
    ExternalTransferDao externalTransferDao;
    @Mock
    JavaMailSender javaMailSender;

    private TransferService transferService = new TransferServiceImpl(transferDao,accountDaoWithCRUD,externalTransferDao,javaMailSender);

    @Test
    public void checkIsEnoughMoneyOnSourceAccountCorrectly() {
        TransferDto transferDto = new TransferDto();
        transferDto.setBalanceBeforeChangeCurrency(BigDecimal.valueOf(1000));

        Account accountFrom = new Account();
        accountFrom.setBalance(BigDecimal.valueOf(1111));
        Assert.assertTrue(transferService.isEnoughMoneyOnSourceAccount(transferDto, accountFrom));
    }

    @Test
    public void checkIsNotEnoughMoneyOnSourceAccountCorrectly() {
        TransferDto transferDto = new TransferDto();
        transferDto.setBalanceBeforeChangeCurrency(BigDecimal.valueOf(1000));

        Account accountFrom = new Account();
        accountFrom.setBalance(BigDecimal.valueOf(800));
        Assert.assertFalse(transferService.isEnoughMoneyOnSourceAccount(transferDto, accountFrom));
    }
}
