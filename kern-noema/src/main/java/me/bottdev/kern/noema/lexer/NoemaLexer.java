package me.bottdev.kern.noema.lexer;

import me.bottdev.kern.noema.token.TokenStream;

import java.io.IOException;

public interface NoemaLexer {


    TokenStream stream() throws IOException;

}
