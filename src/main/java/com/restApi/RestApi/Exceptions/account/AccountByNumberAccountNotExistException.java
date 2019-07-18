package com.restApi.RestApi.Exceptions.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountByNumberAccountNotExistException extends RuntimeException {
    public AccountByNumberAccountNotExistException(String message) {
        super(message);
    }
}
