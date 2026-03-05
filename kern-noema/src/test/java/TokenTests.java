import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.bottdev.kern.noema.token.Token;
import me.bottdev.kern.noema.token.TokenSource;
import me.bottdev.kern.noema.token.source.JacksonTokenSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TokenTests {

    record Format(JsonFactory factory, String content) {}

    private static final String jsonString = """
                {
                    "meta": {
                        "timestamp": 100000000,
                        "version": 1
                    },
                    "name": "Bob",
                    "permissions": [
                        "write",
                        "read",
                        "execute"
                    ]
                }
                """;

    private static final String yamlString = """
                meta:
                    timestamp: 100000000
                    version: 1
                name: Bob
                permissions:
                    - write
                    - read
                    - execute
                """;



    private static Map<String, Format> formats;

    @BeforeAll
    static void init() {
        formats = new HashMap<>();
        formats.put("json", new Format(new JsonFactory(), jsonString));
        formats.put("yaml", new Format(new YAMLFactory(), yamlString));
    }


    @ParameterizedTest
    @ValueSource(strings = {"json", "yaml"})
    public void testJacksonTokenSource(String input) throws IOException {

        System.out.println("Testing JacksonTokenSource for " + input);

        Format format = formats.get(input);
        JsonFactory factory = format.factory;
        String content = format.content;

        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        TokenSource tokenSource = new JacksonTokenSource(factory, inputStream);

        int count = 0;
        Token token = tokenSource.next();
        while (token != null) {
            token = tokenSource.next();
            System.out.println(token);
            count++;

        }

        System.out.println("Found " + count + " tokens for " + input);
        Assertions.assertEquals(17, count);

    }

}
