package com.schibsted.spain.friends.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialException(String message) {
        super(message);
    }

    public InvalidCredentialException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialException(ErrorDto error) {
        super(error);
    }

    public InvalidCredentialException(String message, Integer code) {
        super(message, code);
    }

    public InvalidCredentialException(String message, Integer code, Throwable cause) {
        super(message, code, cause);
    }

    public InvalidCredentialException() {
        super("Invalid credentials");
    }
}
