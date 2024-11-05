package io.github.jelilio.feed.service.impl;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.repository.PostRepository;
import io.github.jelilio.feed.service.PostService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;

  public PostServiceImpl(final PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Override
  public Mono<Post> findById(Long id) {
    return postRepository.findById(id);
  }

  @Override
  public Mono<Post> create(PostDto dto) {
    var post = new Post(dto.title(), dto.body());
    return postRepository.save(post);
  }

  @Override
  public Mono<Post> update(Long id, PostDto dto) {
    return findById(id).flatMap(savedPost -> {
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
}
