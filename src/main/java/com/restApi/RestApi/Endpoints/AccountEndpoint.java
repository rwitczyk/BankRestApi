package com.restApi.RestApi.Endpoints;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Data.UserMagda;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class AccountEndpoint {

    private AccountService accountService;
    private final AccountDao accountDao;

    @Autowired
    public AccountEndpoint(AccountService accountService, AccountDao accountDao) {
        this.accountService = accountService;
        this.accountDao = accountDao;
    }

    @GetMapping("accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return new ResponseEntity<>(accountDao.getAllAccounts(), HttpStatus.OK);
    }

    @GetMapping("accounts/number/{numberAccount}")
    public ResponseEntity<Account> getAccountByNumberAccount(@PathVariable String numberAccount) {
        Account account = accountService.getAccountByNumberAccount(numberAccount);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("accounts/id/{idAccount}")
    public ResponseEntity<Account> getAccountByIdAccount(@PathVariable int idAccount) {
        Account account = accountService.getAccountByIdAccount(idAccount);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("accounts/add")
    public ResponseEntity addAccount(@RequestBody Account account) {
        accountService.addAccount(account);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("accounts/edit/name/{idAccount}")
    public ResponseEntity<Account> editNameAccount(@RequestBody Account accountData, @PathVariable int idAccount) {
        Account account = accountService.changeNameAccountByIdAccount(idAccount, accountData.getName());
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("accounts/delete/{idAccount}")
    public ResponseEntity deleteAccount(@PathVariable int idAccount) {
        accountService.deleteAccountByIdAccount(idAccount);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("accounts/magda")
    public ResponseEntity getAccountsFromMagda() {
        UserMagda[] users = accountService.getUsersFromMagda();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
}
