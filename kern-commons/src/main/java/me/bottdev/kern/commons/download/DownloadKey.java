package me.bottdev.kern.commons.download;

import lombok.NonNull;

import java.net.URI;
import java.nio.file.Path;

/// Key used for download deduplication.
public record DownloadKey(
        @NonNull URI uri,
        @NonNull Path target
) {

    @NonNull
    @Override
    public String toString() {
        return uri + " -> " + target;
    }

}
