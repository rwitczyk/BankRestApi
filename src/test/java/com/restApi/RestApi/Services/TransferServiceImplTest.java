package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.ExternalTransferDao;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Entities.TransferDto;
import com.restApi.RestApi.Exceptions.transfer.SaveNewTransferException;
import com.restApi.RestApi.StatusTransfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
                .balanceAfterChangeCurrency(BigDecimal.valueOf(100))
                .build();
    }

    private Account initializeStubAccount() {
        return Account.builder()
                .id(3)
                .numberAccount("stubNumberAccount")
                .balance(BigDecimal.valueOf(10))
                .currency("PLN")
                .build();
    }

    @Test
    public void shouldChangeStatusOfAllTransfers() {
        // given
        Account account = initializeStubAccount();
        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = Transfer.builder()
                .status(StatusTransfer.CREATED.getValue())
                .balanceAfterChangeCurrency(BigDecimal.valueOf(10))
                .id(4)
                .toAccount(account)
                .build();
        transfers.add(transfer);

        List<Transfer> transfersExpected = new ArrayList<>();
        Transfer transferExpected = Transfer.builder()
                .status(StatusTransfer.DONE.getValue())
                .balanceAfterChangeCurrency(BigDecimal.valueOf(10))
                .id(4)
                .toAccount(account)
                .build();
        transfersExpected.add(transferExpected);

        // when
        when(transferDao.getTransfersByStatus(StatusTransfer.CREATED.getValue())).thenReturn(transfers);
        when(transferDao.save(any())).thenReturn(null);
        when(accountDaoWithCRUD.getAccountById(anyInt())).thenReturn(account);
        transferService.finishTransfers();

        // then
        Assert.assertEquals(transfers.get(0).getStatus(), transfersExpected.get(0).getStatus());
    }

    @Test
    public void shouldAddMoneyToDestinationAccountWhenFinishTransfer(){
        // given
        Account account = Account.builder()
                .balance(BigDecimal.valueOf(1000))
                .build();

        Account accountResult = Account.builder()
                .balance(BigDecimal.valueOf(1010))
                .build();

        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = Transfer.builder()
                .status(StatusTransfer.CREATED.getValue())
                .balanceAfterChangeCurrency(BigDecimal.valueOf(10))
                .id(4)
                .toAccount(account)
                .build();
        transfers.add(transfer);

        // when
        when(transferDao.getTransfersByStatus(StatusTransfer.CREATED.getValue())).thenReturn(transfers);
        when(transferDao.save(any())).thenReturn(null);
        when(accountDaoWithCRUD.getAccountById(anyInt())).thenReturn(account);
        transferService.finishTransfers();

        // then
        Assert.assertEquals(account.getBalance(),accountResult.getBalance());
    }

    @Test
    public void shouldReturnMoneyToSourceAccountIfDestinationAccountNotExistWhenFinishTransfer(){
        // given
        Account accountNotExist = new Account();

        Account accountFrom = Account.builder()
                .balance(BigDecimal.valueOf(1000))
                .id(3)
                .build();

        Account accountResult = Account.builder()
                .id(4)
                .balance(BigDecimal.valueOf(1010))
                .build();

        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = Transfer.builder()
                .status(StatusTransfer.CREATED.getValue())
                .balanceBeforeChangeCurrency(BigDecimal.valueOf(10))
                .balanceAfterChangeCurrency(BigDecimal.valueOf(10))
                .id(1)
                .toAccount(accountNotExist)
                .fromAccount(accountFrom)
                .build();
        transfers.add(transfer);

        // when
        when(transferDao.getTransfersByStatus(StatusTransfer.CREATED.getValue())).thenReturn(transfers);
        when(transferDao.save(any())).thenReturn(null);
        when(accountDaoWithCRUD.getAccountById(1)).thenReturn(null);
        when(accountDaoWithCRUD.getAccountById(3)).thenReturn(accountFrom);
        when(accountDaoWithCRUD.save(any())).thenReturn(null);
        transferService.finishTransfers();

        // then
        Assert.assertEquals(accountFrom.getBalance(),accountResult.getBalance());
    }
}
