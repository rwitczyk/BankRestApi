package com.restApi.RestApi.Services;


import com.restApi.RestApi.Entities.Transfer;

import java.util.List;

public interface TransferService {
    boolean saveNewTransfer(Transfer transfer);

    Iterable<Transfer> getAllTransfers();

    List<Transfer> getTransfersByFromNumberAccount(String numberAccount);

    void finishTransfers();

    List<Transfer> getTransfersByToNumberAccount(String numberAccount);

    void cancelTransfer(Transfer transfer);
}
