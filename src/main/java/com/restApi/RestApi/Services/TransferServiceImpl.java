package com.restApi.RestApi.Services;

import com.restApi.RestApi.Daos.AccountDaoWithCRUD;
import com.restApi.RestApi.Daos.TransferDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

        accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getBalance()));
        accountTo.setBalance(accountTo.getBalance().add(transfer.getBalance()));

        accountDaoWithCRUD.save(accountFrom);
        accountDaoWithCRUD.save(accountTo);

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
