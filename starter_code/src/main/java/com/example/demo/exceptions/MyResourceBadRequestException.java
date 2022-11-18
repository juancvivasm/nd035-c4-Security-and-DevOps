package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MyResourceBadRequestException extends RuntimeException{
    public MyResourceBadRequestException() {
        super();
    }
    public MyResourceBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyResourceBadRequestException(String message) {
        super(message);
    }
    public MyResourceBadRequestException(Throwable cause) {
        super(cause);
    }
}
