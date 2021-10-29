package org.ashton.foodbank.api.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A generic response entity exception handler. All individual response entity exception handler
 * should inherit this class.
 */
@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {
  static final String VALIDATION_FAILED = "Validation failed";
  static final String MESSAGE_SEPARATOR = ": ";
  private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ExceptionResponse> handleAllExceptions(
      Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

    LOG.error("Handling Exception: ", ex);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    List<String> filedErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + MESSAGE_SEPARATOR + e.getDefaultMessage())
            .collect(Collectors.toList());
    List<String> globalErrors =
        ex.getBindingResult().getGlobalErrors().stream()
            .map(e -> e.getObjectName() + MESSAGE_SEPARATOR + e.getDefaultMessage())
            .collect(Collectors.toList());

    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            new Date(),
            VALIDATION_FAILED,
            new ArrayList<String>() {
              {
                addAll(filedErrors);
                addAll(globalErrors);
              }
            });
    return new ResponseEntity<>(exceptionResponse, headers, status);
  }

  /** Map {@link EntityNotFoundException} to HTTP response code 404. */
  @ExceptionHandler(EntityNotFoundException.class)
  public final ResponseEntity<ExceptionResponse> handleEntityNotFoundException(
      Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

    LOG.error("Handling EntityNotFoundException for request {}: ", request, ex);
    return new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
  }

  /** Map {@link BadRequestException} to HTTP response code 400. */
  @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
  public final ResponseEntity<ExceptionResponse> handleBadRequestException(
      Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

    LOG.error("Handling BadRequestException: ", ex);
    return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
    ObjectOptimisticLockingFailureException.class,
    OptimisticLockingFailureException.class,
    DataIntegrityViolationException.class
  })
  public final ResponseEntity<ExceptionResponse> handleHibernateException(
      Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
    LOG.error("Handle Hibernate Error: ", ex);

    return new ResponseEntity<>(exceptionResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public final ResponseEntity<ExceptionResponse> handleHttpMessageConversionException(
      Exception ex, WebRequest request) {
    HttpMessageConversionException conversionException = (HttpMessageConversionException) ex;
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            new Date(),
            conversionException.getMostSpecificCause().getMessage(),
            request.getDescription(false));

    LOG.error("Handling HttpMessageConversionException: ", ex);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e,
      HttpHeaders httpHeaders,
      HttpStatus httpStatus,
      WebRequest webRequest) {
    LOG.error("Handling HttpMessageNotReadableException: ", e);
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            new Date(),
            "Http message not readable. "
                + "This normally means the message cannot be de-serialized. Details: "
                + e.getMostSpecificCause().getMessage(),
            webRequest.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  // Override this method to provide default behavior for all internal server error.
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      Object o,
      HttpHeaders httpHeaders,
      HttpStatus httpStatus,
      WebRequest webRequest) {
    LOG.error("handleExceptionInternal: ", ex);
    if (HttpStatus.INTERNAL_SERVER_ERROR == httpStatus) {
      ExceptionResponse exceptionResponse =
          new ExceptionResponse(new Date(), ex.getMessage(), webRequest.getDescription(false));
      return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      return super.handleExceptionInternal(ex, o, httpHeaders, httpStatus, webRequest);
    }
  }
}
