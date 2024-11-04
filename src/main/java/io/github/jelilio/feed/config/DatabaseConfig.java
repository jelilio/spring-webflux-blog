package io.github.jelilio.feed.config;

import io.github.jelilio.feed.repository.custom.impl.SoftDeleteRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "io.github.jelilio.feed.repository",
    repositoryBaseClass = SoftDeleteRepositoryImpl.class
)
public class DatabaseConfig {
}
