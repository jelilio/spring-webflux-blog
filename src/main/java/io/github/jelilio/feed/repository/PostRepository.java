package io.github.jelilio.feed.repository;

import io.github.jelilio.feed.entity.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface PostRepository extends ReactiveCrudRepository<Post, Long> {

}
