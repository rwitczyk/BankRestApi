package com.restApi.RestApi.Endpoints;

import com.restApi.RestApi.Daos.AccountDao;
import com.restApi.RestApi.Entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountDao accountDao;

    @GetMapping("accounts")
    public List<Account> getAllAccounts()
    {
        return accountDao.getAllAccounts();
    }
}
