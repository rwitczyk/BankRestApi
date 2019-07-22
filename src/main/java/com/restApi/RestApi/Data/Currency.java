package com.restApi.RestApi.Data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class Currency {

    private Map<String, BigDecimal> rates;

    private String base;

    private String date;


}
