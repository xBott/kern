package me.bottdev.kern.commons.download.exceptions;

import lombok.Getter;
import me.bottdev.kern.commons.download.DownloadKey;

@Getter
public class DownloadCancelledException extends DownloadException {

    private final DownloadKey key;

    public DownloadCancelledException(DownloadKey key) {
        super("Download already cancelled: " + key);
        this.key = key;
    }

}
