package roachberry;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RequestRouter {

    public static void main(String[] args) {
        // Kafka Streams configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "request-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.96.213:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Build the topology
        KStreamBuilder builder = new KStreamBuilder();

        // Create a KStream from the source topic (e.g., "source-topic")
        KStream<String, String> jsonStream = builder.stream("request-topic");

        // Use flatMap to split concatenated JSON objects based on "{" and "}"
        KStream<String, String> splitStream = jsonStream
        
        .flatMapValues(value -> {
                List<String> jsonList = new ArrayList<>();
                // Split the input string by occurrences of "{" and "}" to extract individual JSONs
                String[] jsonObjects = value.split("(?<=\\})"); // Split after each closing brace
                
                for (String jsonObject : jsonObjects) {
                    jsonObject = jsonObject.trim(); // Trim any whitespace
                    if (!jsonObject.isEmpty() && jsonObject.startsWith("{") && jsonObject.endsWith("}")) {
                        jsonList.add(jsonObject); // Add each JSON object to the list
                    }
                }
                
                return jsonList; // Return the list of individual JSON objects
            }
        );

        // splitStream.to(Serdes.String(),Serdes.String(),"request-topic");

        // Use branch to route each JSON to different streams based on the "identifier" field
        @SuppressWarnings("unchecked")
        KStream<String, String>[] branches = splitStream.branch(
            (key, value) -> value.contains("\"service\":\"concatena_string\""), // Route to topic A
            (key, value) -> value.contains("\"service\":\"salva_arquivo\""), // Route to topic B
            (key, value) -> value.contains("\"service\":\"calcula_funcao\"") // Route to topic C
        );

        // Send the filtered streams to different topics
        branches[0].to(Serdes.String(),Serdes.String(),"request-concatena-string-topic");
        branches[1].to(Serdes.String(),Serdes.String(),"request-salva-arquivo-topic");
        branches[2].to(Serdes.String(),Serdes.String(),"request-calcula-funcao-topic");

        // Build the Kafka Streams topology
        KafkaStreams streams = new KafkaStreams(builder, props);
        streams.start();

        System.out.println(streams.toString());
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
