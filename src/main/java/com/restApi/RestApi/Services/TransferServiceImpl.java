package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    @Autowired
    TransferDao transferDao;

    @Autowired
    AccountDaoWithCRUD accountDaoWithCRUD;

    @Override
    public Transfer saveNewTransfer(Transfer transferData) {
        Transfer transfer = transferDao.save(transferData);

        Account accountFrom = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getFromNumberAccount());
        Account accountTo = accountDaoWithCRUD.getAccountByNumberAccount(transfer.getToNumberAccount());

        if(accountFrom.getBalance().subtract(transfer.getBalance()).compareTo(BigDecimal.valueOf(0)) == 1) //walidacja czy masz >0 pieniedzy na koncie
        {
            accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getBalance()));
            accountTo.setBalance(accountTo.getBalance().add(transfer.getBalance()));

            accountDaoWithCRUD.save(accountTo);
            accountDaoWithCRUD.save(accountFrom);
        }
        else
        {
            System.out.println("Nie masz wystarczajacej ilosc pieniedzy na koncie!");
        }

        return transfer;
    }

    @Override
    public Iterable<Transfer> getAllTransfers() {
        Iterable<Transfer> transfers = transferDao.findAll();

        return transfers;
    }

    @Override
    public List<Transfer> getTranfersByNumberAccount(String numberAccount) {
        List<Transfer> transfers = transferDao.getTransfersByFromNumberAccount(numberAccount);

        return transfers;
    }
}
