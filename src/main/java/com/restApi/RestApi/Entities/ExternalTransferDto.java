package com.restApi.RestApi.Entities;

import lombok.Data;

@Data
public class ExternalTransferDto {
    String amount;
    String bankName;
    String currency;
    String externalAccount;
    String toAccount;
}
