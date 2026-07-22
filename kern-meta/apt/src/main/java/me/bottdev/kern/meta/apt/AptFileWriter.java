package me.bottdev.kern.meta.apt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.kern.meta.core.FileWriter;
import me.bottdev.kern.meta.core.exceptions.MetaFileOpenException;
import me.bottdev.kern.meta.core.exceptions.MetaFileWriteException;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

@RequiredArgsConstructor
public class AptFileWriter implements FileWriter {

    private final Filer filer;
    private final String path;

    @Getter
    private boolean isOpened;
    private Writer writer;

    @Override
    public void open() throws MetaFileOpenException {
        if (isOpened)
            throw new MetaFileOpenException("File Writer is already opened: " + path);

        try {

            FileObject file = filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    path
            );

            try {
                writer = file.openWriter();

            } catch (Exception ex) {
                throw new MetaFileOpenException("Failed to create file writer: " + path, ex);

            }

            isOpened = true;

        } catch (IOException ex) {
            throw new MetaFileOpenException("Failed to open file: " + path, ex);

        }

    }

    @Override
    public void write(String value) throws MetaFileWriteException {
        if (!isOpened)
            throw new MetaFileWriteException("File Writer is not opened: " + path);

        try {
            writer.write(value);

        } catch (IOException ex) {
            throw new MetaFileWriteException("Failed to write value: " + path);

        }

    }

    @Override
    public void close() throws IOException {
        if (!isOpened)
            throw new IllegalStateException("File Writer is not opened");
        writer.close();
    }
}
