package io.github.jelilio.feed.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

public abstract class AbstractSoftDeletableEntity extends AbstractAuditingEntity {
  private static final String COLUMN_NAME = "deleted_date";

  @JsonIgnore
  @Column(COLUMN_NAME)
  public Instant deletedDate;
}
