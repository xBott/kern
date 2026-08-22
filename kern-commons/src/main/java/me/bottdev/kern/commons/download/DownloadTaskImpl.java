package me.bottdev.kern.commons.download;

import lombok.NonNull;
import me.bottdev.kern.commons.download.exceptions.DownloadCancelledException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final class DownloadTaskImpl implements DownloadTask {

    private static final long MIN_SPEED_SAMPLE_INTERVAL_NANOS = 200_000_000;

    private final DownloadKey key;
    private final CompletableFuture<DownloadResult> completion;
    private final AtomicReference<DownloadState> state = new AtomicReference<>(DownloadState.QUEUED);

    private final AtomicBoolean cancelRequested = new AtomicBoolean(false);
    private volatile Thread executingThread;

    private volatile long total = -1;
    private volatile long downloaded;

    private volatile long startedAt;
    private volatile long completedAt;

    private volatile long lastProgressNanos;
    private volatile long lastProgressBytes;
    private volatile float currentSpeed;

    private final DownloadProgressListener listener;

    public DownloadTaskImpl(
            @NonNull DownloadKey key,
            @NonNull DownloadProgressListener listener
    ) {
        this.key = key;
        this.listener = listener;
        this.completion = new CompletableFuture<>();
    }

    @Override
    public DownloadState state() {
        return state.get();
    }

    @Override
    public DownloadKey key() {
        return key;
    }

    @Override
    public long total() {
        return total;
    }

    @Override
    public long downloaded() {
        return downloaded;
    }

    @Override
    public float speed() {
        return currentSpeed;
    }

    @Override
    public long startedAt() {
        return startedAt;
    }

    @Override
    public long completedAt() {
        return completedAt;
    }

    @Override
    public CompletableFuture<DownloadResult> completion() {
        return completion;
    }

    void bindExecutingThread(Thread thread) {
        this.executingThread = thread;
    }

    boolean isCancelRequested() {
        return cancelRequested.get();
    }

    public void started(long total) {
        if (!state.compareAndSet(DownloadState.QUEUED, DownloadState.DOWNLOADING)) return;
        this.total = total;
        startedAt = System.nanoTime();
        lastProgressNanos = startedAt;
        lastProgressBytes = 0;
        listener.onStarted(key, total);
    }

    void progress(long downloadedSoFar) {

        if (state() != DownloadState.DOWNLOADING) return;

        long now = System.nanoTime();
        long elapsedNanos = now - lastProgressNanos;
        long deltaBytes = downloadedSoFar - lastProgressBytes;

        if (elapsedNanos >= MIN_SPEED_SAMPLE_INTERVAL_NANOS) {
            this.currentSpeed = (float) (deltaBytes / (elapsedNanos / 1_000_000_000.0));
            this.lastProgressNanos = now;
            this.lastProgressBytes = downloadedSoFar;
        }

        this.downloaded = downloadedSoFar;
        listener.onProgress(key, downloadedSoFar, total, currentSpeed);
    }

    public void complete(DownloadResult result) {
        if (!state.compareAndSet(DownloadState.DOWNLOADING, DownloadState.COMPLETED)) return;
        completedAt = System.nanoTime();
        if (total >= 0) downloaded = total;

        if (currentSpeed == 0f && downloaded > 0) {
            double totalSeconds = (completedAt - startedAt) / 1_000_000_000.0;
            if (totalSeconds > 0) {
                currentSpeed = (float) (downloaded / totalSeconds);
            }
        }

        listener.onCompleted(key, result);
        completion.complete(result);
    }

    void fail(Throwable error) {
        DownloadState previous = state.getAndUpdate(s ->
                (s == DownloadState.DOWNLOADING || s == DownloadState.QUEUED) ? DownloadState.FAILED : s);

        if (previous == DownloadState.CANCELLED) {
            completion.completeExceptionally(new DownloadCancelledException(key));
            return;
        }
        if (previous != DownloadState.DOWNLOADING && previous != DownloadState.QUEUED) {
            return;
        }

        this.completedAt = System.nanoTime();
        listener.onFailed(key, error);
        completion.completeExceptionally(error);
    }

    @Override
    public void cancel() {
        DownloadState previous = state.get();

        while (previous == DownloadState.QUEUED || previous == DownloadState.DOWNLOADING) {

            if (state.compareAndSet(previous, DownloadState.CANCELLED)) {
                cancelRequested.set(true);
                this.completedAt = System.nanoTime();
                Thread thread = executingThread;
                if (thread != null) thread.interrupt();
                listener.onCancelled(key);
                return;
            }

            previous = state.get();

        }
    }

}
