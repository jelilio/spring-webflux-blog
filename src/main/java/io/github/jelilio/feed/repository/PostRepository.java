package io.github.jelilio.feed.repository;

import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.repository.custom.SoftDeleteRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, Long>, SoftDeleteRepository {

}
