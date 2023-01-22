package com.example.server.service.validators;

public class ValidateR{

    public static boolean validate(String arg) {
        return (Float.parseFloat(arg) > 0) && (Float.parseFloat(arg) <= 6);
    }
}
