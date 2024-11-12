package io.github.jelilio.feed.repository;

import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.entity.projection.PostWithDeleteStamp;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface PostRepository extends SoftDeleteRepository<Post, Long>, ReactiveCrudRepository<Post, Long> {
  Flux<PostWithDeleteStamp> findByDeletedDateIsNotNull();

  @Query("select * FROM posts")
  Flux<PostWithDeleteStamp> findAllEntries();

  @Query("delete from posts")
  Mono<Void> flush();

  @NonNull
  default Mono<Post> findById(@NonNull Long id) {
    return findById(id, Post.class);
  }

  @NonNull
  default Flux<Post> findAll() {
    return findAll(Post.class);
  }

  @NonNull
  @Transactional
  default Mono<Void> deleteById(@NonNull Long id) {
    return softDeleteById(id, Post.class);
  }

  @NonNull
  @Transactional
  default Mono<Void> deleteAll() {
    return softDeleteAll(Post.class);
  }
}
