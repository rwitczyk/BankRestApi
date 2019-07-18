package com.restApi.RestApi.Exceptions.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountByIdAccountNotExistException extends RuntimeException {
    public AccountByIdAccountNotExistException(String message) {
        super(message);
    }
}
