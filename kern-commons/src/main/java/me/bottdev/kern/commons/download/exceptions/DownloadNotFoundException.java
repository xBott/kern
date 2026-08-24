package me.bottdev.kern.commons.download.exceptions;

import lombok.Getter;
import me.bottdev.kern.commons.download.DownloadKey;

@Getter
public class DownloadNotFoundException extends DownloadException {

    private final DownloadKey key;

    public DownloadNotFoundException(DownloadKey key) {
        super("Resource not found: " + key);
        this.key = key;
    }

}
