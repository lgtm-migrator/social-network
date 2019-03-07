package com.schibsted.spain.friends.utils.exceptions;

import lombok.Getter;

public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final long code;

    public BaseException(String message) {
        super(message);
        this.code = 0;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = 0;
    }

    public BaseException(ErrorDto error) {
        super(error.getMessage());
        this.code = error.getCode();
    }

    public BaseException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
