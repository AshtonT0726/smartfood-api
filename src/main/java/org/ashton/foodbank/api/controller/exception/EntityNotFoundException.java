package org.ashton.foodbank.api.controller.exception;

/** A runtime exception that indicates an entity cannot be found. */
public class EntityNotFoundException extends RuntimeException {
  private static final String EXCEPTION_FORMAT = "%s: %s";

  public EntityNotFoundException(String entityName, String queryCondition) {
    super(String.format(EXCEPTION_FORMAT, entityName, queryCondition));
  }
}
