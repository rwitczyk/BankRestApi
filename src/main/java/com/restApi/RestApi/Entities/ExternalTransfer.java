package com.restApi.RestApi.Entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ExternalTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(targetEntity = Account.class)
    private Account fromAccount;
    private String toAccount;
    @Min(0)
    private BigDecimal amount;
    private String createTransferDate;
}
