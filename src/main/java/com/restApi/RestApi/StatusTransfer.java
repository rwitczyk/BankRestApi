package com.restApi.RestApi;

public enum StatusTransfer {
    CREATED("CREATED"),
    DONE("DONE"),
    CANCELLED("CANCELLED");

    String value;

    StatusTransfer(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
