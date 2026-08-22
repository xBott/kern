package me.bottdev.kern.commons.download;

import me.bottdev.kern.commons.download.exceptions.DownloadCancelledException;
import me.bottdev.kern.commons.download.exceptions.DownloadChecksumException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParallelDownloadManagerTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<InputStream> httpResponse;

    private ParallelDownloadManager downloadManager;

    @BeforeEach
    void setUp() {
        downloadManager = new ParallelDownloadManager(httpClient);
    }

    @Test
    void shouldDownloadFileSuccessfully_whenValidRequest(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        DownloadOptions options = DownloadOptions.builder().build();

        String content = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        when(httpClient.<InputStream>send(any(HttpRequest.class), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(inputStream);
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(
                Map.of("Content-Length", List.of(String.valueOf(content.length()))),
                (s, s2) -> true
        ));

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.COMPLETED);
        assertThat(Files.readString(target)).isEqualTo(content);
        assertThat(downloadManager.isDownloading(new DownloadKey(uri, target))).isFalse();
    }

    @Test
    void shouldThrowException_whenHttpStatusIsNot200(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        DownloadOptions options = DownloadOptions.builder().maxRetries(0).build();

        when(httpClient.<InputStream>send(any(HttpRequest.class), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(404);

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.FAILED);
        assertThatThrownBy(() -> task.completion().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class)
                .hasMessageContaining("Unexpected HTTP status 404");
    }

    @Test
    void shouldRetry_whenDownloadFailsInitially(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        DownloadOptions options = DownloadOptions.builder().maxRetries(1).build();

        String content = "Hello, Retry!";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        when(httpClient.<InputStream>send(any(HttpRequest.class), any()))
                .thenThrow(new IOException("Network error"))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(inputStream);
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(
                Map.of("Content-Length", List.of(String.valueOf(content.length()))),
                (s, s2) -> true
        ));

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.COMPLETED);
        assertThat(Files.readString(target)).isEqualTo(content);
        verify(httpClient, times(2)).<InputStream>send(any(HttpRequest.class), any());
    }

    @Test
    void shouldThrowChecksumException_whenChecksumDoesNotMatch(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");

        String content = "Hello, World!";
        // Intentionally wrong hash
        String expectedHash = "wronghash123";

        DownloadOptions options = DownloadOptions.builder()
                .checksum(DownloadChecksum.Algorithm.SHA_256, expectedHash)
                .maxRetries(0)
                .build();

        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        when(httpClient.<InputStream>send(any(HttpRequest.class), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(inputStream);
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(
                Map.of("Content-Length", List.of(String.valueOf(content.length()))),
                (s, s2) -> true
        ));

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.FAILED);
        assertThatThrownBy(() -> task.completion().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(DownloadChecksumException.class);
        assertThat(Files.exists(target)).isFalse();
    }

    @Test
    void shouldDownloadSuccessfully_whenChecksumMatches(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        String content = "Hello, Checksum!";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String expectedHash = HexFormat.of().formatHex(digest.digest(content.getBytes()));

        DownloadOptions options = DownloadOptions.builder()
                .checksum(DownloadChecksum.Algorithm.SHA_256, expectedHash)
                .build();

        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        when(httpClient.<InputStream>send(any(HttpRequest.class), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(inputStream);
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(
                Map.of("Content-Length", List.of(String.valueOf(content.length()))),
                (s, s2) -> true
        ));

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.COMPLETED);
        assertThat(Files.readString(target)).isEqualTo(content);
    }
    
    @Test
    void shouldNotOverwrite_whenTargetExistsAndOverwriteIsFalse(@TempDir Path tempDir) throws Exception {
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        Files.writeString(target, "Existing File");
        
        DownloadOptions options = DownloadOptions.builder().overwrite(false).maxRetries(0).build();

        // When
        DownloadTask task = downloadManager.download(uri, target, options);

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.FAILED);
        assertThatThrownBy(() -> task.completion().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class)
                .hasMessageContaining("Target already exists and overwrite is disabled");
    }

    @Test
    void shouldCancelDownload_whenCancelIsRequested(@TempDir Path tempDir) throws Exception {
        // Given
        URI uri = URI.create("http://example.com/file.txt");
        Path target = tempDir.resolve("file.txt");
        DownloadOptions options = DownloadOptions.builder().build();

        InputStream slowInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                try {
                    Thread.sleep(100); // Block to simulate slow network
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                return 'a';
            }
        };

        when(httpClient.<InputStream>send(any(HttpRequest.class), any())).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(slowInputStream);
        when(httpResponse.headers()).thenReturn(HttpHeaders.of(Map.of(), (s, s2) -> true));

        // When
        DownloadTask task = downloadManager.download(uri, target, options);
        
        // Let it start downloading
        Thread.sleep(50);
        
        task.cancel();

        waitForTask(task);

        // Then
        assertThat(task.state()).isEqualTo(DownloadState.CANCELLED);
        assertThatThrownBy(() -> task.completion().get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(DownloadCancelledException.class);
    }

    private void waitForTask(DownloadTask task) {
        try {
            task.completion().join();
        } catch (Exception ignored) {
        }
    }
}
