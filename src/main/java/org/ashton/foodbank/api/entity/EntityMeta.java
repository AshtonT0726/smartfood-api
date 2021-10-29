package org.ashton.foodbank.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Each entity should contain version, created time, and update time. All entity classes should
 * extend this class.
 */
@MappedSuperclass
public abstract class EntityMeta {
  public static final String TIME_FORMAT_PATTEN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  @Version protected int version;

  // Use @Convert here because Hibernate older version cannot handle LocalDateTime natively.
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT_PATTEN)
  @CreatedDate
  protected LocalDateTime created;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT_PATTEN)
  @LastModifiedDate
  protected LocalDateTime updated;

  @CreatedBy protected Long createdBy;

  @LastModifiedBy protected Long updatedBy;

  public int getVersion() {
    return version;
  }

  public EntityMeta setVersion(int version) {
    this.version = version;
    return this;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public EntityMeta setCreated(LocalDateTime created) {
    this.created = created;
    return this;
  }

  public LocalDateTime getUpdated() {
    return updated;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public EntityMeta setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public Long getUpdatedBy() {
    return updatedBy;
  }

  public <T extends EntityMeta> void copyCreatedInfo(T other) {
    createdBy = other.createdBy;
    created = other.created;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EntityMeta)) {
      return false;
    }
    EntityMeta that = (EntityMeta) o;
    return version == that.version
        && Objects.equals(created, that.created)
        && Objects.equals(updated, that.updated)
        && Objects.equals(createdBy, that.createdBy)
        && Objects.equals(updatedBy, that.updatedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version);
  }
}
