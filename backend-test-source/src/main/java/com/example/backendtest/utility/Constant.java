package com.example.backendtest.utility;

public class Constant {
    public static final String REQUEST_PATTERN = "[Request: {}]";
    public static final String RESPONSE_PATTERN = "[Response: {}]";
    public static final String EXCEPTION_PATTERN = "With Exception: {}";

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static final String HEADER_STRING = "${jwt.header.string}";
    public static final String TOKEN_PREFIX = "${jwt.token.prefix}";

    public static final String TOKEN_VALIDITY = "${jwt.token.validity}";
    public static final String SIGNING_KEY = "${jwt.signing.key}";
    public static final String AUTHORITIES_KEY = "${jwt.authorities.key}";

}
