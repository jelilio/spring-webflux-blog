package io.github.jelilio.feed.repository.custom.impl;

import io.github.jelilio.feed.entity.base.AbstractSoftDeletableEntity;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;

public class SoftDeleteRepositoryImpl<T extends AbstractSoftDeletableEntity, ID> implements SoftDeleteRepository<T, ID> {
  private final R2dbcEntityOperations entityOperations;
  private final R2dbcConverter converter;

  public SoftDeleteRepositoryImpl(R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
    this.entityOperations = entityOperations;
    this.converter = converter;
  }

  @NonNull
  @Override
  public Mono<Long> count(Class<T> entityClass) {
    return this.entityOperations.count(Query.query(where(getDeletedFieldProperty().getName()).isNull()), entityClass);
  }

  @NonNull
  @Override
  public Mono<Boolean> existsById(@NonNull ID id, Class<T> entityClass) {

    Assert.notNull(id, "Id must not be null");

    var criteria = Criteria.where(getIdProperty(entityClass).getName()).is(id)
        .and(getDeletedFieldProperty().getName()).isNull();

    return this.entityOperations.exists(Query.query(criteria), entityClass);
  }

  @NonNull
  @Override
  public Mono<T> findById(@NonNull ID id, Class<T> entityClass) {
    var criteria = Criteria.where(getIdProperty(entityClass).getName()).is(id)
        .and(getDeletedFieldProperty().getName()).isNull();

    return this.entityOperations.selectOne(Query.query(criteria), entityClass);
  }

  @NonNull
  @Override
  public Flux<T> findAll(Class<T> entityClass) {
    return this.entityOperations.select(Query.query(where(getDeletedFieldProperty().getName()).isNull()), entityClass);
  }

  @NonNull
  @Override
  public Flux<T> findAllById(@NonNull Iterable<ID> iterable, Class<T> entityClass) {
    Assert.notNull(iterable, "The iterable of Id's must not be null");

    return findAllById(Flux.fromIterable(iterable), entityClass);
  }

  @NonNull
  @Override
  public Flux<T> findAllById(@NonNull Publisher<ID> idPublisher, Class<T> entityClass) {
    Assert.notNull(idPublisher, "The Id Publisher must not be null");

    return Flux.from(idPublisher).buffer().filter(ids -> !ids.isEmpty()).concatMap(ids -> {

      if (ids.isEmpty()) {
        return Flux.empty();
      }

      String idProperty = getIdProperty(entityClass).getName();

      var criteria = Criteria.where(idProperty).in(ids)
          .and(getDeletedFieldProperty().getName()).isNull();

      return this.entityOperations.select(Query.query(criteria), entityClass);
    });
  }

  @NonNull
  @Override
  public Flux<T> findAll(@NonNull Sort sort, Class<T> entityClass) {
    Assert.notNull(sort, "Sort must not be null");

    var query = Query.query(where(getDeletedFieldProperty().getName()).isNull());

    return this.entityOperations.select(query.sort(sort), entityClass);
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> softDeleteAll(Class<T> entityClass) {
    return this.entityOperations.update(Query.empty(),
        Update.update(getDeletedFieldProperty().getName(), Instant.now()),
        entityClass
    ).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> softDeleteAllById(@NonNull Iterable<? extends ID> ids, Class<T> entityClass) {
    Assert.notNull(ids, "The iterable of Id's must not be null");

    List<? extends ID> idsList = Streamable.of(ids).toList();

    var query = Query.query(where(getIdProperty(entityClass).getName()).in(idsList));
    var update = Update.update(getDeletedFieldProperty().getName(), Instant.now());

    return this.entityOperations.update(query, update, entityClass).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> softDeleteById(@NonNull ID id, Class<T> entityClass) {
    Assert.notNull(id, "id must not be null");

    var query = Query.query(where(getIdProperty(entityClass).getName()).is(id));
    var update = Update.update(getDeletedFieldProperty().getName(), Instant.now());

    return this.entityOperations.update(query, update, entityClass).then();
  }

  private RelationalPersistentProperty getDeletedFieldProperty() {
    return this.converter
        .getMappingContext()
        .getRequiredPersistentEntity(AbstractSoftDeletableEntity.class)
        .getRequiredPersistentProperty("deletedDate");
  }

  private RelationalPersistentProperty getIdProperty(Class<T> entityClass) {
    return this.converter
        .getMappingContext()
        .getRequiredPersistentEntity(entityClass) //
        .getRequiredIdProperty();
  }
}
