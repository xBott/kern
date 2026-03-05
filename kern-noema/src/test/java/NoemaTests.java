import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class NoemaTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void tokenize() throws JsonProcessingException {

        String jsonString = """
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

        JsonNode node = objectMapper.readTree(jsonString);
        traverseNode(node, 0);

    }

    private void traverseNode(JsonNode node, int indent) {
        String prefix = "    ".repeat(indent);

        if (node.isObject()) {
            System.out.println(prefix + "{");

            node.fieldNames().forEachRemaining(key -> {

                JsonNode value = node.get(key);

                System.out.print(prefix + "    " + key + ": ");

                if (value.isValueNode()) {
                    System.out.println(value.asText());
                } else {
                    System.out.println();
                    traverseNode(value, indent + 2);
                }
            });

            System.out.println(prefix + "}");

        } else if (node.isArray()) {
            System.out.println(prefix + "[");

            for (JsonNode element : node) {
                if (element.isValueNode()) {
                    System.out.println(prefix + "    " + element.asText());
                } else {
                    traverseNode(element, indent + 1);
                }
            }

            System.out.println(prefix + "]");

        } else {
            System.out.println(prefix + node.asText());

        }
    }


}
