package io.github.jelilio.feed.service;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;

public interface PostService {
  Mono<Post> findById(Long id, Locale locale);

  Mono<Post> create(PostDto dto);

  Mono<Post> update(Long id, PostDto dto, Locale locale);

  Flux<Post> findAll();

  Mono<Void> delete(Long id);

  Mono<Void> deleteAll();

  Flux<Post> findAllDeleted();

  Flux<Post> findAllEntries();
}
