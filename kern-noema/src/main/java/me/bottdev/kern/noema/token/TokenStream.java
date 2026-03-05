package me.bottdev.kern.noema.token;

public interface TokenStream {

    boolean isEmpty();

    Token lookahead(int n);

    Token consume();

}
