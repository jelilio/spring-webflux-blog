package io.github.jelilio.feed.repository.custom.impl;

import io.github.jelilio.feed.entity.base.AbstractSoftDeletableEntity;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;

@NoRepositoryBean
public class SoftDeleteRepositoryImpl<T extends AbstractSoftDeletableEntity, ID> extends SimpleR2dbcRepository<T, ID> implements SoftDeleteRepository<T, ID> {
  private final R2dbcEntityOperations entityOperations;
  private final RelationalEntityInformation<T, ID> entity;
  private final Lazy<RelationalPersistentProperty> idProperty;
  private final Lazy<RelationalPersistentProperty> deleteFieldProperty;

  public SoftDeleteRepositoryImpl(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
    super(entity, entityOperations, converter);
    this.entityOperations = entityOperations;
    this.entity = entity;
    this.idProperty = Lazy.of(() -> converter
        .getMappingContext()
        .getRequiredPersistentEntity(this.entity.getJavaType()) //
        .getRequiredIdProperty());
    this.deleteFieldProperty = Lazy.of(() -> converter
        .getMappingContext()
        .getRequiredPersistentEntity(this.entity.getJavaType()) //
        .getRequiredPersistentProperty("deletedDate"));
  }

  @NonNull
  @Override
  public Mono<Long> count() {
    return this.entityOperations.count(Query.query(where(getDeletedFieldProperty().getName()).isNull()),
        this.entity.getJavaType());
  }

  @NonNull
  @Override
  public Mono<Boolean> existsById(@NonNull ID id) {

    Assert.notNull(id, "Id must not be null");

    var criteria = Criteria.where(getIdProperty().getName()).is(id)
        .and(getDeletedFieldProperty().getName()).isNull();

    return this.entityOperations.exists(Query.query(criteria), this.entity.getJavaType());
  }

  @NonNull
  @Override
  public Mono<T> findById(@NonNull ID id) {
    var criteria = Criteria.where(getIdProperty().getName()).is(id)
        .and(getDeletedFieldProperty().getName()).isNull();

    return this.entityOperations.selectOne(Query.query(criteria),
        this.entity.getJavaType());
  }

  @NonNull
  @Override
  public Flux<T> findAll() {
    return this.entityOperations.select(Query
            .query(where(getDeletedFieldProperty().getName()).isNull()),
        this.entity.getJavaType());
  }

  @NonNull
  @Override
  public Flux<T> findAllById(@NonNull Iterable<ID> iterable) {
    Assert.notNull(iterable, "The iterable of Id's must not be null");

    return findAllById(Flux.fromIterable(iterable));
  }

  @NonNull
  @Override
  public Flux<T> findAllById(@NonNull  Publisher<ID> idPublisher) {
    Assert.notNull(idPublisher, "The Id Publisher must not be null");

    return Flux.from(idPublisher).buffer().filter(ids -> !ids.isEmpty()).concatMap(ids -> {

      if (ids.isEmpty()) {
        return Flux.empty();
      }

      String idProperty = getIdProperty().getName();

      var criteria = Criteria.where(idProperty).in(ids)
          .and(getDeletedFieldProperty().getName()).isNull();

      return this.entityOperations.select(Query.query(criteria), this.entity.getJavaType());
    });
  }

  @NonNull
  @Override
  public Flux<T> findAll(@NonNull Sort sort) {
    Assert.notNull(sort, "Sort must not be null");

    var query = Query.query(where(getDeletedFieldProperty().getName()).isNull());

    return this.entityOperations.select(query.sort(sort), this.entity.getJavaType());
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> deleteById(@NonNull Publisher<ID> idPublisher) {
    Assert.notNull(idPublisher, "The Id Publisher must not be null");

    return Flux.from(idPublisher).buffer().filter(ids -> !ids.isEmpty()).concatMap(ids -> {

      if (ids.isEmpty()) {
        return Flux.empty();
      }

      String idProperty = getIdProperty().getName();

      var query = Query.query(Criteria.where(idProperty).in(ids));
      var update = Update.update(getDeletedFieldProperty().getName(), Instant.now());

      return this.entityOperations.update(query, update, this.entity.getJavaType());
    }).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> delete(@NonNull T objectToDelete) {
    Assert.notNull(objectToDelete, "Object to delete must not be null");

    return deleteById(this.entity.getRequiredId(objectToDelete));
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> deleteAll() {
    return this.entityOperations.update(Query.empty(),
        Update.update(getDeletedFieldProperty().getName(), Instant.now()),
        this.entity.getJavaType()
    ).then();
  }

  @NonNull
  @Override
  public Mono<Void> deleteAllById(@NonNull Iterable<? extends ID> ids) {
    Assert.notNull(ids, "The iterable of Id's must not be null");

    List<? extends ID> idsList = Streamable.of(ids).toList();
    String idProperty = getIdProperty().getName();

    var query = Query.query(Criteria.where(idProperty).in(idsList));
    var update = Update.update(getDeletedFieldProperty().getName(), Instant.now());

    return this.entityOperations.update(query, update, this.entity.getJavaType()).then();
  }

  @NonNull
  @Override
  @Transactional
  public Mono<Void> deleteById(@NonNull ID id) {
    Assert.notNull(id, "id must not be null");

    var query = Query.query(Criteria.where(getIdProperty().getName()).is(id));
    var update = Update.update(getDeletedFieldProperty().getName(), Instant.now());

    return this.entityOperations.update(query, update, this.entity.getJavaType()).then();
  }

  private RelationalPersistentProperty getDeletedFieldProperty() {
    return this.deleteFieldProperty.get();
  }

  private RelationalPersistentProperty getIdProperty() {
    return this.idProperty.get();
  }
}
