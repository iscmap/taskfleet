package com.mario.alba.taskfleet.orders.api;

import com.mario.alba.taskfleet.orders.application.IdempotencyKeyConflictException;
import com.mario.alba.taskfleet.orders.application.MissingIdempotencyKeyException;
import com.mario.alba.taskfleet.orders.application.OrderNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MissingIdempotencyKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail missingIdem() {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Missing Idempotency-Key");
        pd.setDetail("Header 'Idempotency-Key' is required for this operation.");
        return pd;
    }

    @ExceptionHandler(IdempotencyKeyConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail idemConflict() {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Idempotency key conflict");
        pd.setDetail("Same Idempotency-Key was used with a different request payload.");
        return pd;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail notFound(RuntimeException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Order not found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation error");
        pd.setDetail("Request validation failed.");
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail constraint(ConstraintViolationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation error");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
