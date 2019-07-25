package com.restApi.RestApi.Converters;

import com.restApi.RestApi.Entities.*;

import java.util.ArrayList;
import java.util.List;

public class ClassConverter {

    public List<TransferDto> convertTransfersToTransfersDto(List<Transfer> transfers) {
        List<TransferDto> transferDtos = new ArrayList<>();
        for (Transfer transfer : transfers
        ) {
            TransferDto transferDto = new TransferDto();
            transferDto.setId(transfer.getId());
            transferDto.setToNumberAccount(transfer.getToAccount().getNumberAccount());
            transferDto.setFromNumberAccount(transfer.getFromAccount().getNumberAccount());
            transferDto.setCreateTransferDate(transfer.getCreateTransferDate());
            transferDto.setExecuteTransferDate(transfer.getExecuteTransferDate());
            transferDto.setStatus(transfer.getStatus());
            transferDto.setCurrencyDestinationAccount(transfer.getCurrencyDestinationAccount());
            transferDto.setBalanceBeforeChangeCurrency(transfer.getBalanceBeforeChangeCurrency());
            transferDto.setBalanceAfterChangeCurrency(transfer.getBalanceAfterChangeCurrency());
            transferDtos.add(transferDto);
        }
        return transferDtos;
    }

    public Transfer convertTransferDtoToTransfer(TransferDto transferData, Account accountFrom, Account accountTo) {
        Transfer transferToSave = new Transfer();
        transferToSave.setId(transferData.getId());
        transferToSave.setFromAccount(accountFrom);
        transferToSave.setToAccount(accountTo);
        transferToSave.setStatus(transferData.getStatus());
        transferToSave.setCurrencyDestinationAccount(transferData.getCurrencyDestinationAccount());
        transferToSave.setBalanceBeforeChangeCurrency(transferData.getBalanceBeforeChangeCurrency());
        transferToSave.setBalanceAfterChangeCurrency(transferData.getBalanceAfterChangeCurrency());
        transferToSave.setCreateTransferDate(transferData.getCreateTransferDate());
        return transferToSave;
    }

    public ExternalTransferDto convertTransferDtoToExternalTransferDto(TransferDto transferDto) {
        ExternalTransferDto externalTransferDto = new ExternalTransferDto();
        externalTransferDto.setAmount(transferDto.getBalanceBeforeChangeCurrency().toString());
        externalTransferDto.setBankName("Robert44");
        externalTransferDto.setCurrency("PLN");
        externalTransferDto.setExternalAccount(transferDto.getFromNumberAccount());
        externalTransferDto.setToAccount(transferDto.getToNumberAccount());

        return externalTransferDto;
    }

    public ExternalTransfer convertTransferDtoToExternalTransfer(TransferDto transferDto, Account accountFrom) {
        ExternalTransfer externalTransfer = new ExternalTransfer();
        externalTransfer.setAmount(transferDto.getBalanceBeforeChangeCurrency());
        externalTransfer.setFromAccount(accountFrom);
        externalTransfer.setToAccount(transferDto.getToNumberAccount());
        externalTransfer.setCreateTransferDate(transferDto.getCreateTransferDate());

        return externalTransfer;
    }
}
