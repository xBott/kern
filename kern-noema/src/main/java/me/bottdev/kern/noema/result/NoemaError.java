package me.bottdev.kern.noema.result;

import lombok.Builder;
import me.bottdev.kern.noema.token.Token;
import me.bottdev.kern.noema.token.TokenPosition;

@Builder
public record NoemaError(
        String message,
        TokenPosition position
) implements NoemaResponse, PositionedResponse {

    public static NoemaError parse(String message, Token token) {
        return NoemaError.builder()
                .message(message)
                .position(token.position())
                .build();
    }

}
