package me.bottdev.kern.commons;

import me.bottdev.kern.commons.exceptions.DisposeException;

public interface Disposable {

    Disposable DISPOSED = new Disposed();

    void dispose() throws DisposeException;

    boolean isDisposed();

    class Disposed implements Disposable {

        @Override
        public void dispose() {

        }

        @Override
        public boolean isDisposed() {
            return true;
        }

    }

}
