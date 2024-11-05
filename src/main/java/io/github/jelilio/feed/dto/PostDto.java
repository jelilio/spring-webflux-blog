package io.github.jelilio.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PostDto(
    @NotBlank @NotEmpty
    @Size(max = 200)
    String title,

    @Size(max = 1000)
    String body
) {
}
