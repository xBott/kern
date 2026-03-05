package me.bottdev.kern.noema.token;

import java.io.IOException;

public interface TokenSource {

    boolean isClosed();

    Token next() throws IOException;

}
