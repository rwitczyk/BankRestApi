package com.restApi.RestApi.Exceptions.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ReturnTransfersByIdAccountException extends RuntimeException {
    public ReturnTransfersByIdAccountException(String message) {
        super(message);
    }
}
