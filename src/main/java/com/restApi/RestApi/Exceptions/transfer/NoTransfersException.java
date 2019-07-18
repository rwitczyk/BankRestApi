package com.restApi.RestApi.Exceptions.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoTransfersException extends RuntimeException {
    public NoTransfersException(String message) {
        super(message);
    }
}
