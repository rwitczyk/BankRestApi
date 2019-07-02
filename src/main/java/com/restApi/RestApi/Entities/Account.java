package com.restApi.RestApi.Entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
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
    private String balance;


    public Account(String numberAccount, String currency, String name, String balance) {
        this.numberAccount = numberAccount;
        this.currency = currency;
        this.name = name;
        this.balance = balance;
    }
}