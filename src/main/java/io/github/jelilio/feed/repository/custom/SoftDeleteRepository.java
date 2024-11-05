package io.github.jelilio.feed.repository.custom;


import org.springframework.data.repository.Repository;

public interface SoftDeleteRepository<T, ID> extends Repository<T, ID> {

}
