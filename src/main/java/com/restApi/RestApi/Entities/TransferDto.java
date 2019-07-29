package com.restApi.RestApi.Entities;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferDto {
    int id;
    BigDecimal balanceBeforeChangeCurrency;
    BigDecimal balanceAfterChangeCurrency;
    String createTransferDate;
    String currencyDestinationAccount;
    String currencyFromAccount;
    int transferToBank;
    String email;
    String executeTransferDate;
    String fromNumberAccount;
    String toNumberAccount;
    String status;
}
