package me.bottdev.kern.commons.download;

import java.util.concurrent.CompletableFuture;

/// Represents an active downloading process.
public interface DownloadTask {

    /// @return State of the task.
    DownloadState state();

    /// @return Download key of the task.
    DownloadKey key();

    /// @return Completable future that allows external code to access result of download asynchronously.
    CompletableFuture<DownloadResult> completion();

    /// @return Total size of a file.
    long total();

    /// @return Size of already downloaded part of a file.
    long downloaded();

    /// @return Speed of download.
    float speed();

    /// @return Time when task was started in nanoseconds.
    long startedAt();

    /// @return Time when task was completed in nanoseconds.
    long completedAt();

    /// Cancels the task.
    void cancel();

}
