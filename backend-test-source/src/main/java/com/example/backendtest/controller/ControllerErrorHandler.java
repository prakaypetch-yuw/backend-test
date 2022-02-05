package com.example.backendtest.controller;

import com.example.backendtest.model.ErrorResponse;
import com.example.backendtest.model.Response;
import com.example.backendtest.utility.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerErrorHandler {
    private static final Logger logger = LogManager.getLogger(ControllerErrorHandler.class);

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<?> bindExceptionHandler(BindException ex) {
        logger.catching(ex);
        return ResponseEntity.badRequest().body(new Response<ErrorResponse>(
                ex.getAllErrors()
                        .stream()
                        .map(error -> new ErrorResponse(error.getCode(), error.getDefaultMessage()))
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        logger.catching(ex);
        return ResponseEntity.badRequest().body(new Response<ErrorResponse>(
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(error -> new ErrorResponse(error.getCode(), error.getDefaultMessage()))
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity<Response> actErrorExceptionHandler(ErrorException ex) {
        logger.error(ex.getErrors());
        logger.error(ex);
        Response response = new Response(ex.getErrors());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Response> defaultExceptionHandler(Exception ex) {
        logger.catching(ex);
        return new ResponseEntity(
                new Response(
                        new ErrorResponse(
                                "500",
                                ex.getMessage()
                        )
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<?> handleParamMissing(MissingServletRequestParameterException ex) {
        logger.error(ex.toString());
        return new ResponseEntity<>(new Response(new ErrorResponse("422", ex.getMessage())),
                HttpStatus.BAD_REQUEST);
    }

}
