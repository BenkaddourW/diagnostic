package com.softwaymedical.diagnostic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Maps index positivity and conversion errors to HTTP 400 problem responses. Other HTTP errors
 * remain handled by Spring's default mechanisms.
 */
@RestControllerAdvice
public class DiagnosticExceptionHandler {

  /**
   * Converts an invalid health index into an HTTP 400 response.
   *
   * @param exception the invalid health index exception
   * @return HTTP 400 with an {@code application/problem+json} body
   */
  @ExceptionHandler(InvalidHealthIndexException.class)
  public ResponseEntity<ProblemDetail> handleInvalidHealthIndex(
      InvalidHealthIndexException exception) {
    return invalidHealthIndexResponse(exception.getMessage());
  }

  /**
   * Reports an index that cannot be converted to a Java {@code int}, without exposing the
   * conversion exception or its internal details.
   *
   * @return HTTP 400 with the accepted numeric range in the problem detail
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleHealthIndexTypeMismatch() {
    return invalidHealthIndexResponse(
        "Health index must be a whole number between 1 and " + Integer.MAX_VALUE + ".");
  }

  private ResponseEntity<ProblemDetail> invalidHealthIndexResponse(String detail) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);

    problem.setTitle("Invalid health index");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }
}
