package me.bottdev.kern.commons.download;

/// State of a [DownloadTask]
public enum DownloadState {
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    CANCELLED
}
