package com.schibsted.spain.friends.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends BaseException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorDto error) {
        super(error);
    }

    public ValidationException(String message, Integer code) {
        super(message, code);
    }

}
