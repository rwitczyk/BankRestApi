package com.restApi.RestApi.Services;


import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Entities.TransferDto;

import java.util.List;

public interface TransferService {

    boolean createTransfer(TransferDto transferdto);

    Iterable<Transfer> getAllTransfers();

    void finishTransfers();

    List<TransferDto> getTransfersByFromNumberAccount(String numberAccount);

    List<TransferDto> getTransfersByToNumberAccount(String numberAccount);

    void cancelTransfer(Transfer transfer);
}
