package com.virutsa.code.weatherapp.util;

import java.math.BigDecimal;

public class Utils {

    public static String dblValue(String value) {
        if (value != null && !value.isEmpty()) {
            return String.format("%.2f", new BigDecimal(String.valueOf(value)));
        } else
            return "0";
    }
}
