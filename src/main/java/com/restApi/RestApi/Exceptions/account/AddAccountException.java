package com.restApi.RestApi.Exceptions.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AddAccountException extends RuntimeException {
    public AddAccountException(String message) {
        super(message);
    }
}
