package com.schibsted.spain.friends.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ErrorDto error) {
        super(error);
    }

    public BusinessException(String message, Integer code) {
        super(message, code);
    }

    public BusinessException(String message, Integer code, Throwable cause) {
        super(message, code, cause);
    }

}
