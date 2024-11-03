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
    Post post = new Post("This is a test message");

    Post created = postRepository.save(post).block();

    assert created != null;
    StepVerifier.create(postRepository.findById(created.getId()))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(post.message, result.message, "message is NOT equal");
          return true;
        })
        .verifyComplete();
  }

  @Test
  public void canSavePostEntity() {
    Post post = new Post("This is a test message");

    StepVerifier.create(postRepository.save(post))
        .thenConsumeWhile((result) -> {
          assertNotNull(result);
          assertNotNull(result.getId());
          assertEquals(post.message, result.message, "message is NOT the equal");
          return true;
        })
        .verifyComplete();
  }
}
