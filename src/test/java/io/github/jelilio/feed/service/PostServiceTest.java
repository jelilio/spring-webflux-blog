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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@DirtiesContext
public class PostServiceTest {
  @Autowired
  private PostRepository postRepository;

  private PostService postService;

  @BeforeEach
  public void setUp() {
    postService = new PostServiceImpl(postRepository);

    Flux<Post> deleteAndInsert = postService.deleteAll()
        .thenMany(postRepository.saveAll(
            Flux.just(
                new Post("This is a demo message-1"),
                new Post("This is a demo message-2"),
                new Post("This is a demo message-3")
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
    var postDto = new PostDto("this is a demo message");

    StepVerifier.create(postService.create(postDto))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(postDto.message(), result.message, "message is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canCreateThenFindPost() {
    var postDto = new PostDto("this is a demo message");

    Mono<Post> createAndUpdate = postService.create(postDto)
        .flatMap(it -> postService.findById(it.getId()));

    StepVerifier.create(createAndUpdate)
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(postDto.message(), result.message, "message is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canCreateThenUpdatePost() {
    var postDto = new PostDto("this is a demo message");
    var updatedDto = new PostDto("this is a demo updated message");

    Mono<Post> createAndUpdate = postService.create(postDto)
        .flatMap(it -> postService.update(it.getId(), updatedDto));

    StepVerifier.create(createAndUpdate)
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(updatedDto.message(), result.message, "message is NOT equal");
          return true;
        })
        .verifyComplete();
  }
}
