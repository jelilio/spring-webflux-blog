package io.github.jelilio.feed.entity.projection;

import java.time.Instant;

public record PostWithDeleteStamp (
    Long id,
    String title,
    String body,
    Instant deletedDate
) {

}
