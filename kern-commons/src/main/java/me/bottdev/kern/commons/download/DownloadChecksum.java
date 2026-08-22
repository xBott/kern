package me.bottdev.kern.commons.download;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

public record DownloadChecksum(
    @NonNull Algorithm algorithm,
    @NonNull Supplier<String> expectedSupplier
) {

    @RequiredArgsConstructor
    public enum Algorithm {
        SHA_256("SHA-256");

        private final String algorithmName;

        public String algorithmName() {
            return algorithmName;
        }

    }

}

