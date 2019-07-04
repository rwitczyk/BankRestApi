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

    private String fromNumberAccount;
    private String toNumberAccount;
    @Min(0)
    private BigDecimal balance;

    private String currency;
    private String createTransferDate;
    private String executeTransferDate;
    private String status;
}
