package me.bottdev.kern.noema.token;

public record Token(
        TokenType tokenType,
        String lexeme,
        TokenPosition position
) {}
