package me.bottdev.kern.commons.download;

import lombok.NonNull;

public record DownloadOptions(
        boolean overwrite,
        DownloadChecksum checksum,
        int maxRetries,
        int bufferSize,
        @NonNull DownloadProgressListener listener
) {

    public static class Builder {

        private boolean overwrite = false;
        private DownloadChecksum checksum = null;
        private int maxRetries = 0;
        private int bufferSize = 8192;
        private DownloadProgressListener listener = DownloadProgressListener.NOOP;

        public Builder overwrite(boolean overwrite) {
            this.overwrite = overwrite;
            return this;
        }

        public Builder checksum(DownloadChecksum checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder checksum(
                @NonNull DownloadChecksum.Algorithm algorithm,
                @NonNull String expected
        ) {
            this.checksum = new DownloadChecksum(algorithm, expected);
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder listener(DownloadProgressListener listener) {
            this.listener = listener;
            return this;
        }

        public DownloadOptions build() {
            return new DownloadOptions(
                    overwrite,
                    checksum,
                    maxRetries,
                    bufferSize,
                    listener
            );
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
