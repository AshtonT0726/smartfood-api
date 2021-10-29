package org.ashton.foodbank.api.controller.exception;

/** A runtime exception that indicates a bad request. */
public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }
}
