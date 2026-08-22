package me.bottdev.kern.commons.download;

/// Listens events happened with a concrete download task.
public interface DownloadProgressListener {

    DownloadProgressListener NOOP = new DownloadProgressListener() {};

    default void onStarted(DownloadKey key, long totalBytes) {}

    /// @param speedBytesPerSecond current (sampled) speed of download.
    default void onProgress(DownloadKey key, long downloadedBytes, long totalBytes, float speedBytesPerSecond) {}

    default void onCompleted(DownloadKey key, DownloadResult result) {}

    default void onFailed(DownloadKey key, Throwable error) {}

    default void onCancelled(DownloadKey key) {}
}
