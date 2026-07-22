package me.bottdev.kern.meta.apt;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.FileFactory;
import me.bottdev.kern.meta.core.FileWriter;

import javax.annotation.processing.Filer;

@RequiredArgsConstructor
public class AptFileFactory implements FileFactory {

    private final Filer filer;

    @Override
    public FileWriter createWriter(String path) {
        return new AptFileWriter(filer, path);
    }

}
