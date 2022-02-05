package com.example.backendtest.type;

import com.example.backendtest.model.ErrorTypeBase;
import lombok.Getter;

@Getter
public enum ErrorType implements ErrorTypeBase {

    INVALID_SALARY("40001", "salary is less than limit"),

    USER_NOT_FOUND("40401", "user not found"),

    USER_ALREADY_EXISTS("40901", "user already exists"),

    ;

    private final String code;
    private final String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
