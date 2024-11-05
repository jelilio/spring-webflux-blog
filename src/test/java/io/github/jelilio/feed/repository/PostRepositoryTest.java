package io.github.jelilio.feed.repository;

import io.github.jelilio.feed.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
public class PostRepositoryTest {
  @Autowired
  PostRepository postRepository;

  @Test
  public void canFindPostById() {
    Post post = new Post("This is a title", "This is a test body");

    Post created = postRepository.save(post).block();

    assert created != null;
    StepVerifier.create(postRepository.findById(created.getId()))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(post.title, result.title, "title is NOT equal");
          assertEquals(post.body, result.body, "body is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canSavePostEntity() {
    Post post = new Post("This is a title", "This is a test body");

    StepVerifier.create(postRepository.save(post))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(post.title, result.title, "title is NOT equal");
          assertEquals(post.body, result.body, "body is NOT equal");
          return true;
        })
        .verifyComplete();
  }
}
