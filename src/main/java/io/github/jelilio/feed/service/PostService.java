package io.github.jelilio.feed.service;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
  Mono<Post> findById(Long id);

  Mono<Post> create(PostDto dto);

  Mono<Post> update(Long id, PostDto dto);

  Flux<Post> findAll();

  Mono<Void> delete(Long id);

  Mono<Void> deleteAll();
}
