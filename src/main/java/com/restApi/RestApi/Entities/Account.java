package com.restApi.RestApi.Entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Min;
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
    private String numberAccount;
   // @Size(min = 26)
  //  @Size(max = 26)

    @Column(name = "currency")
    private String currency;

    @Column(name = "name")
    private String name;

    @Column(name = "balance")
    @Min(0)
    private BigDecimal balance;
}