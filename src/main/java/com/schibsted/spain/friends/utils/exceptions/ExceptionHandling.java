package com.schibsted.spain.friends.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandling extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(BusinessException ex, WebRequest request) {
        return buildResponse(ex.getCode(), ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AlreadyExistsException.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(AlreadyExistsException ex, WebRequest request) {
        return buildResponse(ex.getCode(), ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(ValidationException ex, WebRequest request) {
        return buildResponse(ex.getCode(), ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(UnauthorizedException ex, WebRequest request) {
        return buildResponse(ex.getCode(), ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(NotFoundException ex, WebRequest request) {
        return buildResponse(ex.getCode(), ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    protected ResponseEntity<ErrorDto> handleConflict(Exception ex, WebRequest request) {
        return buildResponse(-1, ex, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDto> buildResponse(long code, Exception ex, HttpStatus status) {
        ErrorDto errorDto = ErrorDto.builder()
                .code(code)
                .message(ex.getMessage())
                .exceptionClass(ex.getClass().getSimpleName())
                .build();
        return new ResponseEntity<>(errorDto, status);
    }
}
