package io.github.jelilio.feed.repository.custom;


import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SoftDeleteRepository<T, ID> {
  @NonNull
  Mono<Long> count(Class<T> entityClass);

  @NonNull
  Mono<Boolean> existsById(@NonNull ID id, Class<T> entityClass);

  @NonNull
  Mono<T> findById(@NonNull ID id, Class<T> entityClass);

  @NonNull
  Flux<T> findAll(Class<T> entityClass);

  @NonNull
  Flux<T> findAllById(@NonNull Iterable<ID> iterable, Class<T> entityClass);

  @NonNull
  Flux<T> findAllById(@NonNull Publisher<ID> idPublisher, Class<T> entityClass);

  @NonNull
  Flux<T> findAll(@NonNull Sort sort, Class<T> entityClass);

  @NonNull
  @Transactional
  Mono<Void> softDeleteAll(Class<T> entityClass);

  @NonNull
  Mono<Void> softDeleteAllById(@NonNull Iterable<? extends ID> ids, Class<T> entityClass);

  @NonNull
  @Transactional
  Mono<Void> softDeleteById(@NonNull ID id, Class<T> entityClass);
}
