package io.github.jelilio.feed.controller;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/posts")
public class PostController {
  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping
  public Flux<Post> getAll(){
    return postService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Post> get(@PathVariable("id") Long id) {
    return postService.findById(id);
  }

  @PostMapping
  @ResponseStatus(value = CREATED)
  public Mono<ResponseEntity<Post>> create(@RequestBody @Valid PostDto dto) {
    return postService
        .create(dto).map(created -> ResponseEntity
            .created(URI.create("/posts/%d".formatted(created.getId())))
            .body(created));
  }

  @PutMapping("/{id}")
  public Mono<Post> update(@PathVariable("id") Long id, @RequestBody @Valid PostDto dto){
    return postService.update(id, dto);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(value = NO_CONTENT)
  public Mono<Void> delete(@PathVariable("id") Long id) {
    return postService.delete(id);
  }
}
