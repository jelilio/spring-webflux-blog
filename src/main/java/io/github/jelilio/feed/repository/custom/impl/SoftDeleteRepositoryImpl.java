package io.github.jelilio.feed.repository.custom.impl;

import io.github.jelilio.feed.entity.base.AbstractSoftDeletableEntity;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.springframework.data.relational.core.query.Criteria.where;

@NoRepositoryBean
public class SoftDeleteRepositoryImpl<T extends AbstractSoftDeletableEntity, ID> extends SimpleR2dbcRepository<T, ID> implements SoftDeleteRepository {
  private final R2dbcEntityOperations entityTemplate;
  private final RelationalEntityInformation<T, ID> entityInformation;
  private final Lazy<RelationalPersistentProperty> idProperty;
  private final Lazy<RelationalPersistentProperty> deleteFieldProperty;

  public SoftDeleteRepositoryImpl(RelationalEntityInformation<T, ID> entityInformation, R2dbcEntityOperations entityOperations, R2dbcConverter converter) throws NoSuchFieldException {
    super(entityInformation, entityOperations, converter);
    this.entityTemplate = entityOperations;
    this.entityInformation = entityInformation;
    this.idProperty = Lazy.of(() -> converter //
        .getMappingContext() //
        .getRequiredPersistentEntity(this.entityInformation.getJavaType()) //
        .getRequiredIdProperty());
    this.deleteFieldProperty = Lazy.of(() -> converter //
        .getMappingContext() //
        .getRequiredPersistentEntity(this.entityInformation.getJavaType()) //
        .getRequiredPersistentProperty("deletedDate"));
  }


  @NonNull
  @Override
  public Mono<T> findById(@NonNull ID id) {
    var criteria = Criteria.where(getIdProperty().getName()).is(id)
        .and(getDeletedFieldProperty().getName()).isNull();

    return this.entityTemplate.selectOne(Query.query(criteria),
        this.entityInformation.getJavaType());
  }

  @NonNull
  @Override
  public Flux<T> findAll() {
    return this.entityTemplate.select(Query
            .query(where(getDeletedFieldProperty().getName()).isNull()),
        this.entityInformation.getJavaType());
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> delete(@NonNull T entity) {
    Assert.notNull(entity, "entity must not be null");

    entity.deletedDate = Instant.now();

    return entityTemplate.update(entity).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> deleteAll() {
    Flux<T> flux =  findAll().map(it -> {
      it.deletedDate = Instant.now();
      return it;
    });

    return saveAll(flux).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> deleteById(@NonNull ID id) {
    return findById(id).flatMap(this::delete);
  }

  private RelationalPersistentProperty getDeletedFieldProperty() {
    return this.deleteFieldProperty.get();
  }

  private RelationalPersistentProperty getIdProperty() {
    return this.idProperty.get();
  }
}
