package com.SPE.miniproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.SPE.miniproject.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Division by zero
    @ExceptionHandler(DivisionByZeroException.class)
    public ResponseEntity<ErrorResponse> handleDivisionByZero(DivisionByZeroException ex) {
        ErrorResponse body = new ErrorResponse(400, "Bad Request", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // Missing required query param e.g. /add?a=5 (missing b)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = "Missing required parameter: " + ex.getParameterName();
        ErrorResponse body = new ErrorResponse(400, "Bad Request", msg);
        return ResponseEntity.badRequest().body(body);
    }

    // Wrong type e.g. /add?a=abc&b=3
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = "Invalid value for parameter '" + ex.getName() + "': must be a number";
        ErrorResponse body = new ErrorResponse(400, "Bad Request", msg);
        return ResponseEntity.badRequest().body(body);
    }
}
