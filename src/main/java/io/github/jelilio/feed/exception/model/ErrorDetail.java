package io.github.jelilio.feed.exception.model;

import java.time.Instant;

public record ErrorDetail(
    int status,
    String message,
    Instant timestamp
) {
}
