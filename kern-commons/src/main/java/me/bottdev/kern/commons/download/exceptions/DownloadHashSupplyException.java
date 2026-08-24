package me.bottdev.kern.commons.download.exceptions;

import lombok.Getter;
import me.bottdev.kern.commons.download.DownloadKey;

@Getter
public class DownloadHashSupplyException extends DownloadException {

    private final DownloadKey key;

    public DownloadHashSupplyException(DownloadKey key, Throwable cause) {
        super("Failed to supply checksum hash for: " + key);
        this.key = key;
    }

}
