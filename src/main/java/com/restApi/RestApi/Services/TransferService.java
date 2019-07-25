package com.restApi.RestApi.Services;


import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Entities.Transfer;
import com.restApi.RestApi.Entities.TransferDto;

import java.util.List;

public interface TransferService {

    boolean createTransfer(TransferDto transferDto, Account accountFrom);

    void createExteriorTransfer(TransferDto transferDto, Account accountFrom);

    void chooseTransfer(TransferDto transferDto);

    Iterable<Transfer> getAllTransfers();

    void finishTransfers();

    List<TransferDto> getTransfersByFromNumberAccount(String numberAccount);

    List<TransferDto> getTransfersByToNumberAccount(String numberAccount);

    void cancelTransfer(TransferDto transferDto);

    boolean isEnoughMoneyOnSourceAccount(TransferDto transferDto, Account accountFrom);
}
