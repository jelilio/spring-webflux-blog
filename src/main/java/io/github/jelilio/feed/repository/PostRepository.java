package io.github.jelilio.feed.repository;

import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.entity.projection.PostWithDeleteStamp;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, Long>, SoftDeleteRepository<Post, Long> {
  Flux<PostWithDeleteStamp> findByDeletedDateIsNotNull();

  @Query("select * FROM posts")
  Flux<PostWithDeleteStamp> findAllEntries();

  @Query("delete from posts")
  Mono<Void> flush();
}
