package io.github.jelilio.feed.entity;

import io.github.jelilio.feed.entity.base.AbstractSoftDeletableEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("posts")
public class Post extends AbstractSoftDeletableEntity {
  @Id
  private Long id;

  @Column("title")
  public String title;

  @Column("body")
  public String body;

  public Post() {}

  public Post(String title, String body) {
    this.title = title;
    this.body = body;
  }

  public Long getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Post post = (Post) o;
    return Objects.equals(id, post.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
