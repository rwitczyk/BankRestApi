package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.ExternalTransferDao;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.TransferDto;
import com.restApi.RestApi.Exceptions.transfer.SaveNewTransferException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

public class TransferServiceImplTest {
    private TransferDao transferDao;
    private AccountDaoWithCRUD accountDaoWithCRUD;
    private ExternalTransferDao externalTransferDao;
    private JavaMailSender javaMailSender;
    private TransferService transferService;

    @Before
    public void setUp() throws Exception {
        javaMailSender = mock(JavaMailSender.class);
        accountDaoWithCRUD = mock(AccountDaoWithCRUD.class);
        transferDao = mock(TransferDao.class);
        externalTransferDao = mock(ExternalTransferDao.class);
        transferService = new TransferServiceImpl(transferDao, accountDaoWithCRUD, externalTransferDao, javaMailSender);
    }

    @Test(expected = SaveNewTransferException.class)
    public void shouldThrowExceptionWhenThereIsNotEnoughMoneyOnSourceAccountWhenCreatingTransfer() {
        // given
        Account stubAccount = initializeStubAccount();
        TransferDto stubTransferDto = initializeStubTransferDto();

        // when
        transferService.createTransfer(stubTransferDto, stubAccount);
    }

    private TransferDto initializeStubTransferDto() {
        return TransferDto.builder()
                .balanceBeforeChangeCurrency(BigDecimal.valueOf(100))
                .build();
    }

    private Account initializeStubAccount() {
        return Account.builder()
                .numberAccount("stubNumberAccount")
                .balance(BigDecimal.valueOf(10))
                .currency("PLN")
                .build();
    }
}
