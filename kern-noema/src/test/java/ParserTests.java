import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.bottdev.kern.noema.ast.*;
import me.bottdev.kern.noema.ast.nodes.LeafValueNode;
import me.bottdev.kern.noema.ast.visitors.PrettyPrintVisitor;
import me.bottdev.kern.noema.parser.NoemaParser;
import me.bottdev.kern.noema.parser.RecursiveNoemaParser;
import me.bottdev.kern.noema.token.TokenSource;
import me.bottdev.kern.noema.token.TokenStream;
import me.bottdev.kern.noema.token.source.JacksonTokenSource;
import me.bottdev.kern.noema.token.stream.BufferedTokenStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParserTests {

    record Format(JsonFactory factory, String content) {}

    private static final String bigJsonString = """
                {
                          "stationId": "ST-4821",
                          "location": {
                            "country": "Slovakia",
                            "city": "Bratislava",
                            "coordinates": {
                              "lat": 48.1486,
                              "lon": 17.1077
                            }
                          },
                          "status": "ACTIVE",
                          "current": {
                            "temperature": 21.4,
                            "humidity": 63,
                            "pressure": 1012,
                            "wind": {
                              "speed": 5.6,
                              "direction": "NW"
                            }
                          },
                          "alerts": [
                            {
                              "type": "HUMIDITY_HIGH",
                              "threshold": 80,
                              "active": false
                            },
                            {
                              "type": "LOW_PRESSURE",
                              "threshold": 990,
                              "active": false
                            }
                          ],
                          "sensors": [
                            {
                              "id": "S1",
                              "type": "humidity",
                              "units": "%",
                              "history": [
                                { "timestamp": "2026-02-23T10:00:00Z", "value": 60 },
                                { "timestamp": "2026-02-23T11:00:00Z", "value": 63 },
                                { "timestamp": "2026-02-23T12:00:00Z", "value": 65 }
                              ]
                            },
                            {
                              "id": "S2",
                              "type": "temperature",
                              "units": "C",
                              "history": [
                                { "timestamp": "2026-02-23T10:00:00Z", "value": 20.1 },
                                { "timestamp": "2026-02-23T11:00:00Z", "value": 21.4 },
                                { "timestamp": "2026-02-23T12:00:00Z", "value": 22.0 }
                              ]
                            }
                          ],
                          "maintenanceDates": [
                            "2025-12-01",
                            "2026-01-15",
                            "2026-03-10"
                          ],
                          "metadata": {
                            "createdBy": "NoemaSystem",
                            "version": 2,
                            "tags": ["weather", "humidity", "iot", "monitoring"]
                          }
                        }
    """;

    private static String bigYamlString = """
            stationId: ST-4821
            location:
              country: Slovakia
              city: Bratislava
              coordinates:
                lat: 48.1486
                lon: 17.1077
            status: ACTIVE
            current:
              temperature: 21.4
              humidity: 63
              pressure: 1012
              wind:
                speed: 5.6
                direction: NW
            alerts:
              - type: HUMIDITY_HIGH
                threshold: 80
                active: false
              - type: LOW_PRESSURE
                threshold: 990
                active: false
            sensors:
              - id: S1
                type: humidity
                units: "%"
                history:
                  - timestamp: "2026-02-23T10:00:00Z"
                    value: 60
                  - timestamp: "2026-02-23T11:00:00Z"
                    value: 63
                  - timestamp: "2026-02-23T12:00:00Z"
                    value: 65
              - id: S2
                type: temperature
                units: "C"
                history:
                  - timestamp: "2026-02-23T10:00:00Z"
                    value: 20.1
                  - timestamp: "2026-02-23T11:00:00Z"
                    value: 21.4
                  - timestamp: "2026-02-23T12:00:00Z"
                    value: 22.0
            maintenanceDates:
              - "2025-12-01"
              - "2026-01-15"
              - "2026-03-10"
            metadata:
              createdBy: NoemaSystem
              version: 2
              tags:
                - weather
                - humidity
                - iot
                - monitoring
            """;

    private static final String brokenJsonString = """
            {
                "test2": 12
            }
    """;

    private static Map<String, Format> formats;

    @BeforeAll
    static void init() {
        formats = new HashMap<>();
        formats.put("json", new Format(new JsonFactory(), bigJsonString));
        formats.put("yaml", new Format(new YAMLFactory(), bigYamlString));
        formats.put("broken_json", new Format(new JsonFactory(), brokenJsonString));
    }

    @ParameterizedTest
    @ValueSource(strings = {"json", "yaml", "broken_json"})
    public void testBuildNoemaSyntaxTree(String input) {

        System.out.println("Testing Noema Syntax Tree for " + input);

        Format format = formats.get(input);
        JsonFactory factory = format.factory;
        String content = format.content;

        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        TokenSource tokenSource = new JacksonTokenSource(factory, inputStream);
        TokenStream tokenStream = new BufferedTokenStream(tokenSource);

        NoemaParser noemaParser = new RecursiveNoemaParser(tokenStream);
        noemaParser.parse().ifSuccessOrElse(
                tree -> {
                    PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();

                },
                responses -> {
                    System.out.printf("Noema Parser returned %dx responses:\n", responses.size());
                    responses.forEach(response ->
                            System.out.println(" - " + response.message())
                    );
                }
        );

    }

}
