package me.bottdev.kern.meta.core;

import me.bottdev.kern.meta.core.exceptions.MetaFileOpenException;
import me.bottdev.kern.meta.core.exceptions.MetaFileWriteException;

public interface FileWriter extends AutoCloseable {

    boolean isOpened();

    void open() throws MetaFileOpenException;

    void write(String value) throws MetaFileWriteException;

}
