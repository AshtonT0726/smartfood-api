package org.ashton.foodbank.api.controller.exception;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/** A class used to communicate an exception in the rest response. */
public class ExceptionResponse {
  private final Date timestamp;
  private final String message;
  private final String details;

  public ExceptionResponse(Date timestamp, String message, String details) {
    this.timestamp = timestamp;
    this.message = message;
    this.details = details;
  }

  public ExceptionResponse(Date timestamp, String message, Collection<String> errors) {
    this(timestamp, message, errors.stream().collect(Collectors.joining(", ", "{", "}")));
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

  public String getDetails() {
    return details;
  }

  @Override
  public String toString() {
    return "ExceptionResponse{"
        + "timestamp="
        + timestamp
        + ", message='"
        + message
        + '\''
        + ", details='"
        + details
        + '\''
        + '}';
  }
}
