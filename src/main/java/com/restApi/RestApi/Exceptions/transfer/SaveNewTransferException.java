package com.restApi.RestApi.Exceptions.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaveNewTransferException extends RuntimeException {
    public SaveNewTransferException(String message) {
        super(message);
    }
}
