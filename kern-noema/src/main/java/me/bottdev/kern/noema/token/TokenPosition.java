package me.bottdev.kern.noema.token;

import lombok.Builder;

@Builder
public record TokenPosition(
        int line,
        int column
) {}
