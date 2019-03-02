package com.schibsted.spain.friends.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(ErrorDto error) {
        super(error);
    }

    public AlreadyExistsException(String message, Integer code) {
        super(message, code);
    }

}
