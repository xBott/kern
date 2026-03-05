package me.bottdev.kern.noema.token.stream;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.noema.token.Token;
import me.bottdev.kern.noema.token.TokenSource;
import me.bottdev.kern.noema.token.TokenStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BufferedTokenStream implements TokenStream {

    private final TokenSource source;
    private final List<Token> buffer = new ArrayList<>();
    private int position = 0;

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty() && source.isClosed();
    }

    @Override
    public Token lookahead(int n) {
        int target = position + n;

        while (buffer.size() <= target) {

            try {
                Token next = source.next();
                if (next == null) {
                    return null;
                }
                buffer.add(next);

            } catch (IOException ex) {
                return null;
            }

        }

        return buffer.get(target);
    }

    @Override
    public Token consume() {
        Token token = lookahead(0);
        if (token != null) {
            position++;
        }
        return token;
    }
}