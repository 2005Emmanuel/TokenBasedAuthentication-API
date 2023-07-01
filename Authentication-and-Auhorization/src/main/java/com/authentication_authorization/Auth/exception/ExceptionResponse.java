package com.authentication_authorization.Auth.exception;

import java.util.Date;

import lombok.Data;

@Data
public class ExceptionResponse {
    int statusCode;
    Date timestamp;
    String message;
    String details;
    
    public ExceptionResponse(int statusCode, Date timestamp, String message, String details){
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
