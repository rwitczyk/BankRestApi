package com.restApi.RestApi.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ExternalTransferDto {
    String amount;
    String bankName;
    String currency;
    String externalAccount;
    String toAccount;
}
