package com.restApi.RestApi.Data;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Currency {

    private Map<String, Double> rates;

    private String base;

    private String date;


}
