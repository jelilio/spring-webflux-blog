package io.github.jelilio.feed.controller.admin;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.repository.PostRepository;
import io.github.jelilio.feed.service.PostService;
import io.github.jelilio.feed.service.impl.PostServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class PostControllerTest {
  @Autowired
  WebTestClient webTestClient;

  @Autowired
  MessageSource messageSource;

  @Autowired
  PostRepository postRepository;

  PostService postService;

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
    StepVerifier.create(postService.flush())
        .verifyComplete();
  }

  @Test
  public void testFetchDeletedPosts() {
    PostDto dto = new PostDto("this is a title","this is a demo body");

    Post created = postService.create(dto).block();
    assertNotNull(created);

    postService.delete(created.getId()).block();

    webTestClient
        .get().uri("/admin/posts?deleted")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$", hasSize(1));
  }

  @Test
  public void testFetchAllPostByAdmin() {
    PostDto dto = new PostDto("this is a title","this is a demo body");

    Post created = postService.create(dto).block();
    assertNotNull(created);

    postService.delete(created.getId()).block();

    webTestClient
        .get().uri("/admin/posts")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$", hasSize(4));
  }
}
