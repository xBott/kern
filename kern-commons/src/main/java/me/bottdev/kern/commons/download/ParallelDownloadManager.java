package me.bottdev.kern.commons.download;

import me.bottdev.kern.commons.download.exceptions.DownloadCancelledException;
import me.bottdev.kern.commons.download.exceptions.DownloadChecksumException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Implementation of [DownloadManager] that uses [java.util.concurrent.ExecutorService] for concurrent downloading.
public class ParallelDownloadManager implements DownloadManager {

    private final HttpClient httpClient;
    private final ExecutorService executorService;

    private final ConcurrentHashMap<DownloadKey, DownloadTask> activeDownloads = new ConcurrentHashMap<>();

    public ParallelDownloadManager(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public boolean isDownloading(DownloadKey key) {
        return activeDownloads.containsKey(key);
    }

    @Override
    public DownloadTask getTask(DownloadKey key) {
        return activeDownloads.get(key);
    }

    @Override
    public List<DownloadTask> getTasks() {
        return activeDownloads.values().stream().toList();
    }

    @Override
    public DownloadTask download(
            URI uri,
            Path target,
            DownloadOptions options
    ) {

        DownloadKey key = new DownloadKey(uri, target);

        return activeDownloads.compute(key, (k, existing) -> {
            if (existing != null && (existing.state() == DownloadState.QUEUED || existing.state() == DownloadState.DOWNLOADING)) {
                return existing;
            }
            DownloadTaskImpl task = new DownloadTaskImpl(key, options.listener());
            executorService.submit(() -> executeWithRetry(task, options));
            return task;
        });

    }

    private void executeWithRetry(DownloadTaskImpl task, DownloadOptions options) {
        task.bindExecutingThread(Thread.currentThread());

        int attempt = 0;
        Throwable lastError = null;

        while (attempt <= options.maxRetries()) {

            if (task.isCancelRequested()) {
                task.fail(new DownloadCancelledException(task.key()));
                activeDownloads.remove(task.key());
                return;
            }

            try {
                execute(task, options);
                activeDownloads.remove(task.key());
                return;

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                task.fail(new DownloadCancelledException(task.key()));
                activeDownloads.remove(task.key());
                return;

            } catch (Exception ex) {
                lastError = ex;
                attempt++;
                task.progress(0);
            }
        }

        task.fail(lastError);
        activeDownloads.remove(task.key());

    }

    private void execute(DownloadTaskImpl task, DownloadOptions options) throws
            IOException,
            InterruptedException,
            DownloadChecksumException
    {

        DownloadKey key = task.key();
        Path target = key.target();

        if (Files.exists(target) && !options.overwrite()) {
            throw new IOException("Target already exists and overwrite is disabled: " + target);
        }

        Path tempFile = target.resolveSibling(target.getFileName().toString() + ".part");
        Files.createDirectories(target.toAbsolutePath().getParent());

        HttpRequest request = HttpRequest.newBuilder(key.uri()).GET().build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException("Unexpected HTTP status " + response.statusCode() + " for " + key.uri());
        }

        long total = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        task.started(total);

        MessageDigest digest = createDigestIfNeeded(options);
        long copied;

        try (InputStream in = response.body();
             OutputStream fileOut = Files.newOutputStream(tempFile);
             OutputStream out = digest != null ? new DigestOutputStream(fileOut, digest) : fileOut) {

            byte[] buffer = new byte[options.bufferSize()];
            long totalCopied = 0;
            int read;

            while ((read = in.read(buffer)) != -1) {

                if (Thread.interrupted() || task.isCancelRequested()) {
                    throw new InterruptedException("Download cancelled: " + key.uri());
                }

                out.write(buffer, 0, read);
                totalCopied += read;
                task.progress(totalCopied);
            }

            copied = totalCopied;

        } catch (IOException | InterruptedException ex) {
            Files.deleteIfExists(tempFile);
            throw ex;
        }

        String actualHash = digest != null ? HexFormat.of().formatHex(digest.digest()) : null;
        String expectedHash = options.checksum() != null ? options.checksum().expectedSupplier().get() : null;

        if (actualHash != null && expectedHash != null && !expectedHash.equalsIgnoreCase(actualHash)) {
            Files.deleteIfExists(tempFile);
            throw new DownloadChecksumException(key, expectedHash, actualHash);
        }

        Files.move(tempFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        task.complete(new DownloadResult(target, copied, System.nanoTime() - task.startedAt(), actualHash));
    }

    private MessageDigest createDigestIfNeeded(DownloadOptions options) {

        DownloadChecksum checksum = options.checksum();
        if (checksum == null) return null;

        String algorithmName = checksum.algorithm().algorithmName();
        try {
            return MessageDigest.getInstance(algorithmName);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(algorithmName + " not available.", ex);

        }
    }

}
