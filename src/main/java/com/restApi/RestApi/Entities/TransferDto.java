package com.restApi.RestApi.Entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDto {
    int id;
    BigDecimal balanceBeforeChangeCurrency;
    BigDecimal balanceAfterChangeCurrency;
    String createTransferDate;
    String currencyDestinationAccount;
    String currencyFromAccount;
    int transferToBank;
    String executeTransferDate;
    String fromNumberAccount;
    String toNumberAccount;
    String status;
}
