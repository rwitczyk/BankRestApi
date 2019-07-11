package com.restApi.RestApi.Services;


import com.restApi.RestApi.Entities.Transfer;

import java.util.List;

public interface TransferService {
    boolean saveNewTransfer(Transfer transfer);

    Iterable<Transfer> getAllTransfers();

    List<Transfer> getTranfersByNumberAccount(String numberAccount);

    void finishTransfers();
}
