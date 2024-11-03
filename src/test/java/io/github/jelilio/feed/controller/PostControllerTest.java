package io.github.jelilio.feed.controller;

import io.github.jelilio.feed.dto.PostDto;
import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.repository.PostRepository;
import io.github.jelilio.feed.service.PostService;
import io.github.jelilio.feed.service.impl.PostServiceImpl;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class PostControllerTest {
  @Autowired
  WebTestClient webTestClient;

  @Autowired
  PostRepository postRepository;

  PostService postService;

  @BeforeEach
  public void setUp() {
    postService = new PostServiceImpl(postRepository);
  }

  @AfterEach
  public void cleanup() {
    StepVerifier.create(postService.deleteAll())
        .verifyComplete();
  }

  @Test
  public void testCreatePost() {
    PostDto post = new PostDto("this is a demo message");

    webTestClient
        .post().uri("/posts")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(post), PostDto.class)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isNotEmpty()
        .jsonPath("$.message").isEqualTo(post.message());
  }

  @Test
  public void testFindEmptyPosts() {
    webTestClient
        .get().uri("/posts")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$").isEmpty();
  }

  @Test
  public void testFindPostsById() {
    PostDto dto = new PostDto("this is a demo message");

    Post created = postService.create(dto).block();

    assertNotNull(created);

    webTestClient
        .get().uri("/posts/{id}", created.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(created.getId())
        .jsonPath("$.message").isEqualTo(dto.message());
  }

  @Test
  public void testUpdatePosts() {
    PostDto dto = new PostDto("this is a demo message");
    PostDto updatedDto = new PostDto("this is a updated demo message");

    Post created = postService.create(dto).block();

    assertNotNull(created);

    webTestClient
        .put().uri("/posts/{id}", created.getId())
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .body(Mono.just(updatedDto), PostDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(created.getId())
        .jsonPath("$.message").isEqualTo(updatedDto.message());
  }

  @Test
  public void testDeletePostById() {
    PostDto dto = new PostDto("this is a demo message");

    Post created = postService.create(dto).block();

    assertNotNull(created);

    webTestClient
        .delete().uri("/posts/{id}", created.getId())
        .exchange()
        .expectStatus().isNoContent()
        .expectBody();
  }
}