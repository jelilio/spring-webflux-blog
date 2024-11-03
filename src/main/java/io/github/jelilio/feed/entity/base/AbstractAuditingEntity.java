package io.github.jelilio.feed.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

public abstract class AbstractAuditingEntity {
  @JsonIgnore
  @CreatedBy @Column("created_by")
  public String createdBy = "";

  @JsonIgnore
  @LastModifiedBy @Column("last_modified_by")
  public String lastModifiedBy = "";

  @JsonIgnore
  @CreatedDate @Column("created_date")
  public Instant createdDate = Instant.now();

  @JsonIgnore
  @LastModifiedDate @Column("last_modified_date")
  public Instant lastModifiedDate = Instant.now();
}