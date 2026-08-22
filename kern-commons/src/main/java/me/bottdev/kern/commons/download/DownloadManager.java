package me.bottdev.kern.commons.download;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/// Manages all downloads, responsible for concurrency, retry logic.
public interface DownloadManager {

    /// @return Indicates whether the manager already downloading a file.
    boolean isDownloading(DownloadKey key);

    /// @return Active download task or null.
    DownloadTask getTask(DownloadKey key);

    /// @return Unmodifiable list of all active download tasks.
    List<DownloadTask> getTasks();

    /// Downloads file from specified uri to the specified location asynchronously.
    /// Method checks duplicated downloads.
    /// @return New download task or active download task if such download is already active.
    DownloadTask download(
            URI uri,
            Path target,
            DownloadOptions options
    );

}
