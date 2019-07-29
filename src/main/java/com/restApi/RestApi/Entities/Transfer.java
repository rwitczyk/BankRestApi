package com.restApi.RestApi.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = Account.class)
    private Account fromAccount;
    @ManyToOne(targetEntity = Account.class)
    private Account toAccount;
    @Min(0)
    private BigDecimal balanceAfterChangeCurrency;

    @Min(0)
    private BigDecimal balanceBeforeChangeCurrency;

    private String currencyDestinationAccount;
    private String currencyFromAccount;
    private String createTransferDate;
    private String executeTransferDate;
    private String status;
}
