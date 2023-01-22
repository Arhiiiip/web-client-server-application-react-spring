package com.example.server.service.validators;

public class ValidateXY{

    public static boolean validate(String arg) {
        return (Float.parseFloat(arg) >= -7) && (Float.parseFloat(arg) <= 7);
    }
}
