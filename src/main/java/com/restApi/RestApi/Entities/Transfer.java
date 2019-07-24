package com.restApi.RestApi.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
