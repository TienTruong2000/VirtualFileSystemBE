package org.tientt.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.tientt.constants.MessageConstant;
import org.tientt.controllers.payloads.ErrorResponse;
import org.tientt.utils.MessageUtil;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        return buildErrorResponse(ex, MessageUtil.getMessage(MessageConstant.Exception.INTERNAL_ERROR), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus) {
        return ErrorResponse.build(httpStatus, message, exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }

}
