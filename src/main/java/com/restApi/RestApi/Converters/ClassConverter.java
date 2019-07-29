package com.restApi.RestApi.Converters;

import com.restApi.RestApi.Entities.*;

import java.util.ArrayList;
import java.util.List;

public class ClassConverter {

    public List<TransferDto> convertTransfersToTransfersDto(List<Transfer> transfers) {
        List<TransferDto> transferDtos = new ArrayList<>();
        for (Transfer transfer : transfers
        ) {
            TransferDto transferDto = TransferDto.builder()
                    .toNumberAccount(transfer.getToAccount().getNumberAccount())
                    .fromNumberAccount(transfer.getFromAccount().getNumberAccount())
                    .createTransferDate(transfer.getCreateTransferDate())
                    .executeTransferDate(transfer.getExecuteTransferDate())
                    .status(transfer.getStatus())
                    .currencyDestinationAccount(transfer.getCurrencyDestinationAccount())
                    .currencyFromAccount(transfer.getCurrencyFromAccount())
                    .balanceBeforeChangeCurrency(transfer.getBalanceBeforeChangeCurrency())
                    .balanceAfterChangeCurrency(transfer.getBalanceAfterChangeCurrency())
                    .build();

            transferDtos.add(transferDto);
        }
        return transferDtos;
    }

    public Transfer convertTransferDtoToTransfer(TransferDto transferData, Account accountFrom, Account accountTo) {
        return Transfer.builder()
                .fromAccount(accountFrom)
                .toAccount(accountTo)
                .status(transferData.getStatus())
                .currencyDestinationAccount(transferData.getCurrencyDestinationAccount())
                .currencyFromAccount(transferData.getCurrencyFromAccount())
                .balanceBeforeChangeCurrency(transferData.getBalanceBeforeChangeCurrency())
                .balanceAfterChangeCurrency(transferData.getBalanceAfterChangeCurrency())
                .createTransferDate(transferData.getCreateTransferDate())
                .build();
    }

    public ExternalTransferDto convertTransferDtoToExternalTransferDto(TransferDto transferDto) {
        return ExternalTransferDto.builder()
                .amount(transferDto.getBalanceBeforeChangeCurrency().toString())
                .bankName("Robert44")
                .currency("PLN")
                .externalAccount(transferDto.getFromNumberAccount())
                .toAccount(transferDto.getToNumberAccount())
                .build();
    }

    public ExternalTransfer convertTransferDtoToExternalTransfer(TransferDto transferDto, Account accountFrom) {
        return ExternalTransfer.builder()
                .amount(transferDto.getBalanceBeforeChangeCurrency())
                .fromAccount(accountFrom)
                .toAccount(transferDto.getToNumberAccount())
                .createTransferDate(transferDto.getCreateTransferDate())
                .build();
    }
}
