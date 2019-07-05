package com.restApi.RestApi.Entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountID")
    private int id;

    @Column(name = "numberAccount") // nazwa ktora bedzie w bazce w tabeli
    @Size(min = 4,max = 4)
    private String numberAccount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    @Min(0)
    private BigDecimal balance;

    public Account(@Size(min = 4, max = 4) String numberAccount, String currency, String name, @Min(0) BigDecimal balance) {
        this.numberAccount = numberAccount;
        this.currency = currency;
        this.name = name;
        this.balance = balance;
    }
}