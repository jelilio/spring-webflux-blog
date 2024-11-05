package io.github.jelilio.feed.service;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.repository.PostRepository;
import io.github.jelilio.feed.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostServiceTest {
  @Autowired
  private PostRepository postRepository;

  @Autowired
  private MessageSource messageSource;

  private PostService postService;

  @BeforeEach
  public void setUp() {
    postService = new PostServiceImpl(messageSource, postRepository);

    Flux<Post> deleteAndInsert = postService.deleteAll()
        .thenMany(postRepository.saveAll(
            Flux.just(
                new Post("This is title-1", "This is a demo body-1"),
                new Post("This is title-2", "This is a demo body-2"),
                new Post("This is title-3", "This is a demo body-3")
            )));

    StepVerifier.create(deleteAndInsert)
        .expectNextCount(3)
        .verifyComplete();
  }

  @AfterEach
  public void cleanup() {
    StepVerifier.create(postRepository.deleteAll())
        .verifyComplete();
  }

  @Test
  public void canCreatePost() {
    PostDto postDto = new PostDto("this is a title","this is a demo body");

    StepVerifier.create(postService.create(postDto))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(postDto.title(), result.title, "title is NOT equal");
          assertEquals(postDto.body(), result.body, "body is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canCreateThenFindPost() {
    PostDto postDto = new PostDto("this is a title","this is a demo body");

    Mono<Post> createAndUpdate = postService.create(postDto)
        .flatMap(it -> postService.findById(it.getId(), Locale.getDefault()));

    StepVerifier.create(createAndUpdate)
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(postDto.title(), result.title, "title is NOT equal");
          assertEquals(postDto.body(), result.body, "body is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canCreateThenUpdatePost() {
    PostDto postDto = new PostDto("this is a title","this is a demo body");
    PostDto updatedDto = new PostDto("this is the updated title", "this is a updated demo body");

    Mono<Post> createAndUpdate = postService.create(postDto)
        .flatMap(it -> postService.update(it.getId(), updatedDto, Locale.getDefault()));

    StepVerifier.create(createAndUpdate)
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(updatedDto.title(), result.title, "title is NOT equal");
          assertEquals(updatedDto.body(), result.body, "body is NOT equal");
          return true;
        })
        .verifyComplete();
  }
}
