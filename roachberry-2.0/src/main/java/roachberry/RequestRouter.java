package roachberry;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RequestRouter {

    public static void main(String[] args) {
        // Configuração do Kafka Streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "request-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.99:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.AT_LEAST_ONCE); // Garantias de processamento

        // Construção da topologia
        StreamsBuilder builder = new StreamsBuilder();

        // Criar um KStream a partir do tópico de origem
        KStream<String, String> jsonStream = builder.stream("request-topic");

        // Usar flatMapValues para dividir JSONs concatenados
        KStream<String, String> splitStream = jsonStream.flatMapValues(value -> {
            List<String> jsonList = new ArrayList<>();
            // Dividir o input em objetos JSON individuais
            String[] jsonObjects = value.split("(?<=\\})"); // Divide após cada '}'

            for (String jsonObject : jsonObjects) {
                jsonObject = jsonObject.trim();
                if (!jsonObject.isEmpty() && jsonObject.startsWith("{") && jsonObject.endsWith("}")) {
                    jsonList.add(jsonObject); // Adiciona JSON válido
                }
            }
            return jsonList;
        }, Named.as("SplitStream"));

        // Dividir o stream em ramos baseados no campo "service"
        KStream<String, String>[] branches = splitStream.branch(
            Named.as("BranchingLogic"),
            (key, value) -> value.contains("\"service\":\"concatena_string\""), // Ramo para "concatena_string"
            (key, value) -> value.contains("\"service\":\"salva_arquivo\""),   // Ramo para "salva_arquivo"
            (key, value) -> value.contains("\"service\":\"calcula_funcao\"")  // Ramo para "calcula_funcao"
        );

        // Enviar cada ramo para um tópico diferente
        branches[0].to("request-concatena-string-topic", Produced.with(Serdes.String(), Serdes.String()));
        branches[1].to("request-salva-arquivo-topic", Produced.with(Serdes.String(), Serdes.String()));
        branches[2].to("request-calcula-funcao-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Criar a instância do Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.setUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Erro inesperado na thread " + thread.getName() + ": " + exception.getMessage());
        });
        streams.start();

        System.out.println("Kafka Streams iniciado com sucesso!");

        // Adicionar um gancho de encerramento
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando Kafka Streams...");
            streams.close();
        }));
    }
}
