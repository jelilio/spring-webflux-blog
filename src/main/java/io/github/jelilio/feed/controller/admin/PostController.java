package io.github.jelilio.feed.controller.admin;

import io.github.jelilio.feed.entity.Post;
import io.github.jelilio.feed.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/admin/posts")
@RestController("AdminController")
public class PostController {
  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping()
  public Flux<Post> getAll(){
    return postService.findAllEntries();
  }

  @GetMapping(params = "deleted")
  public Flux<Post> getAllDeleted(){
    return postService.findAllDeleted();
  }
}
