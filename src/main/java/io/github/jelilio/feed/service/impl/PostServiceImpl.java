package io.github.jelilio.feed.service.impl;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.exception.NotFoundException;
import io.github.jelilio.feed.repository.PostRepository;
import io.github.jelilio.feed.service.PostService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Service
public class PostServiceImpl implements PostService {
  private final MessageSource messageSource;
  private final PostRepository postRepository;

  public PostServiceImpl(MessageSource messageSource, final PostRepository postRepository) {
    this.messageSource = messageSource;
    this.postRepository = postRepository;
  }

  @Override
  public Mono<Post> findById(Long id, Locale locale) {
    return postRepository.findById(id)
        .switchIfEmpty(Mono.error(
            new NotFoundException(messageSource
                .getMessage("post-id.not.exist", new Long[]{id}, locale))
        ));
  }

  @Override
  public Mono<Post> create(PostDto dto) {
    var post = new Post(dto.title(), dto.body());
    return postRepository.save(post);
  }

  @Override
  public Mono<Post> update(Long id, PostDto dto, Locale locale) {
    return findById(id, locale).flatMap(savedPost -> {
      savedPost.title = dto.title();
      savedPost.body = dto.body();
      return postRepository.save(savedPost);
    });
  }

  @Override
  public Flux<Post> findAll() {
    return postRepository.findAll();
  }

  @Override
  public Mono<Void> delete(Long id) {
    return postRepository.deleteById(id);
  }

  @Override
  public Mono<Void> deleteAll() {
    return postRepository.deleteAll();
  }

  @Override
  public Flux<Post> findAllDeleted() {
    return postRepository.findByDeletedDateIsNotNull();
  }

  @Override
  public Flux<Post> findAllEntries() {
    return postRepository.findAllEntries();
  }
}
