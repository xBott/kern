package me.bottdev.kern.commons.download;

import lombok.NonNull;

import java.nio.file.Path;

public record DownloadResult(
        @NonNull Path path,
        long bytesDownloaded,
        long durationNanos,
        String hash
) {}
