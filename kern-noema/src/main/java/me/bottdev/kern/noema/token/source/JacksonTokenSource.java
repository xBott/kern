package me.bottdev.kern.noema.token.source;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.RequiredArgsConstructor;
import me.bottdev.kern.noema.token.Token;
import me.bottdev.kern.noema.token.TokenPosition;
import me.bottdev.kern.noema.token.TokenSource;
import me.bottdev.kern.noema.token.TokenType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RequiredArgsConstructor
public class JacksonTokenSource implements TokenSource {

    private final JsonFactory jsonFactory;
    private final InputStream inputStream;

    private JsonParser parser;

    private JsonParser createParser() throws IOException {
        return jsonFactory.createParser(inputStream);
    }

    @Override
    public boolean isClosed() {
        return parser != null && parser.isClosed();
    }

    @Override
    public Token next() throws IOException {
        if (parser == null) {
            try {
                parser = createParser();
            } catch (IOException e) {
                throw new IOException("Failed to create json parser: ", e);
            }
        }

        if (isClosed()) return null;

        try {
            JsonToken jsonToken = parser.nextToken();
            if (jsonToken == null) return null;

            JsonLocation location = parser.currentLocation();

            return buildToken(location, jsonToken);

        } catch (IOException ex) {
            throw new IOException("Failed to retrieve next token: ", ex);

        }


    }

    private Token buildToken(JsonLocation location, JsonToken jsonToken) throws IOException {
        int lineNumber = location.getLineNr();
        int columnNumber = location.getColumnNr();
        TokenPosition position = new TokenPosition(lineNumber, columnNumber);

        String lexeme;
        TokenType tokenType;

        switch (jsonToken) {
            case FIELD_NAME -> {
                lexeme = parser.getText();
                tokenType = TokenType.KEY;
            }

            case VALUE_STRING,
                 VALUE_NUMBER_INT,
                 VALUE_NUMBER_FLOAT,
                 VALUE_TRUE,
                 VALUE_FALSE,
                 VALUE_NULL -> {
                lexeme = parser.getText();
                tokenType = TokenType.VALUE;
            }

            case START_OBJECT -> {
                lexeme = "{";
                tokenType = TokenType.SECTION_START;
            }
            case END_OBJECT -> {
                lexeme = "}";
                tokenType = TokenType.SECTION_END;
            }

            case START_ARRAY -> {
                lexeme = "[";
                tokenType = TokenType.LIST_START;
            }
            case END_ARRAY -> {
                lexeme = "]";
                tokenType = TokenType.LIST_END;
            }

            default -> {
                lexeme = jsonToken.name();
                tokenType = TokenType.VALUE;
            }
        }

        return new Token(tokenType, lexeme, position);
    }

}
