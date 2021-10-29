package org.ashton.foodbank.api.controller;

import com.google.common.base.Preconditions;
import org.ashton.foodbank.api.controller.exception.BadRequestException;
import org.ashton.foodbank.api.controller.exception.EntityNotFoundException;
import org.ashton.foodbank.api.entity.AutoFieldsUtil;
import org.ashton.foodbank.api.entity.EntityMeta;
import org.ashton.foodbank.api.entity.interfaces.HasId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** An abstract class which has some common implementation for CRUD controllers. */
public abstract class CrudController<T extends EntityMeta & HasId> {
  private static final String ENTITY_AND_PATH_ID_NOT_MATCH =
      "Entity Id %d does not match the Id %d on the URL path.";

  protected final JpaRepository<T, Long> entityRepository;

  protected CrudController(JpaRepository<T, Long> entityRepository) {
    this.entityRepository = entityRepository;
  }

  protected static <P> void validateImmutablePropertyForUpdate(
      P oldProp, P newProp, String propName) {
    if (newProp != null && !newProp.equals(oldProp)) {
      throw new BadRequestException(
          String.format("Property %s cannot be changed during update.", propName));
    }
  }

  /** Find the entity. */
  public static <E> E findEntity(JpaRepository<E, Long> repository, Long id, String entityName) {
    Optional<E> optional = repository.findById(id);
    if (!optional.isPresent()) {
      throw new EntityNotFoundException(entityName, "id" + id);
    }
    return optional.get();
  }

  protected abstract String getEntityName();

  /** Find the entity. */
  public T findEntity(Long id) {
    return findEntity(entityRepository, id, getEntityName());
  }

  /**
   * Validate the new entity during the entity creation. Subclass is expected to add more
   * validations.
   */
  public void validateEntityForCreate(T newEntity) {
    Preconditions.checkArgument(newEntity.getId() == null, "Id should not be provided.");
    AutoFieldsUtil.checkAutoFieldsNotSetInCreation(newEntity);
  }

  /** Validate the entityId in the updateEntity matches the one on the URL. */
  public void validateEntityIdForUpdate(T updatedEntity, Long id) {
    if (updatedEntity.getId() != null && !updatedEntity.getId().equals(id)) {
      throw new BadRequestException(
          String.format(ENTITY_AND_PATH_ID_NOT_MATCH, updatedEntity.getId(), id));
    }
  }

  /** Validate the entity during the entity update. */
  public void validateEntityForUpdate(T updatedEntity, T originalEntity) {
    AutoFieldsUtil.checkAutoFieldsNotSet(updatedEntity, originalEntity);
  }
}
