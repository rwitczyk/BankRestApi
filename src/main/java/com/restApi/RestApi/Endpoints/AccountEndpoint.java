package com.restApi.RestApi.Endpoints;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Entities.Account;
import com.restApi.RestApi.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AccountEndpoint {

    private AccountService accountService;

    @Autowired
    public AccountEndpoint(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    private AccountDao accountDao;

    @GetMapping("accounts")
    public List<Account> getAllAccounts()
    {
        return accountDao.getAllAccounts();
    }

    @GetMapping("accounts/number/{numberAccount}")
    public ResponseEntity<Account> getAccountByNumberAccount(@PathVariable String numberAccount)
    {
        Account account = accountService.getAccountByNumberAccount(numberAccount);
        if(account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("accounts/id/{idAccount}")
    public ResponseEntity<Account> getAccountByIdAccount(@PathVariable int idAccount)
    {
        Account account = accountService.getAccountByIdAccount(idAccount);
        if(account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("accounts/add")
    public void addAccount(@RequestBody Account account)
    {
        accountDao.addAccount(account);
    }

    @PostMapping("accounts/edit/name/{idAccount}")
    public ResponseEntity<Account> editNameAccount(@RequestBody Account accountData,@PathVariable int idAccount)
    {
       Account account = accountService.changeNameAccountByIdAccount(idAccount,accountData);

        if(account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
