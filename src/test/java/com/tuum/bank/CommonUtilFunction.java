package com.tuum.bank;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtilFunction {
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
