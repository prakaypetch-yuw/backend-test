package com.example.backendtest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    public List<ErrorResponse> errors = new ArrayList();
    private T data;

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public Response(ErrorResponse error) {
        this.errors = Arrays.asList(error);
    }

    public Response(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public Response(ErrorTypeBase errorType) {
        this.errors = Collections.singletonList(new ErrorResponse(errorType.getCode(), errorType.getMessage()));
    }

    public Response(ErrorTypeBase errorType, Object... args) {
        this.errors = Collections.singletonList(new ErrorResponse(errorType.getCode(), String.format(errorType.getMessage(), args)));
    }

    public Response(T data, List<ErrorResponse> errors) {
        this.data = data;
        this.errors = errors;
    }
}
