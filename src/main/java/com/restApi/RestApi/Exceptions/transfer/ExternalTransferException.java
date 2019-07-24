package com.restApi.RestApi.Exceptions.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExternalTransferException extends RuntimeException {
    public ExternalTransferException(String message) {
        super(message);
    }
}
