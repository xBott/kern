package me.bottdev.kern.commons.download.exceptions;

import lombok.Getter;
import me.bottdev.kern.commons.download.DownloadKey;

@Getter
public class DownloadChecksumException extends DownloadException {

    private final DownloadKey key;
    private final String expected;
    private final String actual;

    public DownloadChecksumException(DownloadKey key, String expected, String actual) {
        super("Checksum mismatch for: " + key + ". Expected: " + expected + ", Actual: " + actual);
        this.key = key;
        this.expected = expected;
        this.actual = actual;
    }

}
