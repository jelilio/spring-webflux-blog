package io.github.jelilio.feed.exception.handler;


import io.github.jelilio.feed.exception.NotFoundException;
import io.github.jelilio.feed.exception.model.ErrorDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ExceptionHandlers {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorDetail> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(NOT_FOUND).body(
        new ErrorDetail(NOT_FOUND.value(), ex.getMessage(), Instant.now())
    );
  }
}
