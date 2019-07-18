package com.restApi.RestApi.Exceptions.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReturnTransferByNumberAccountException extends RuntimeException {
    public ReturnTransferByNumberAccountException(String message) {
        super(message);
    }
}
