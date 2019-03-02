package com.schibsted.spain.friends.utils.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {

    private long code;
    private String msg;
    private String exceptionClass;

}
