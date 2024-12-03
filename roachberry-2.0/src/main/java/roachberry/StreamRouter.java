package roachberry;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

public class StreamRouter {

    public static void main(String[] args) {
        // Configuração do Kafka Streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "200.235.84.122:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        // Construção da topologia
        StreamsBuilder builder = new StreamsBuilder();

        // Etapa 1: Processar mensagens de `qr-code-content-topic` e enviar para `source-db-topic`
        KStream<String, String> qrCodeStream = builder.stream("qr-code-content-topic");
        qrCodeStream.to("source-db-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Etapa 2: Processar mensagens de `source-db-topic` e enviar para `save-data-db-topic`
        KStream<String, String> saveDataStream = builder.stream("source-db-topic");
        saveDataStream.to("save-data-db-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Etapa 3: Processar mensagens de `save-data-db-topic` e enviar para `source-light-topic`
        KStream<String, String> sourceLightStream = builder.stream("save-data-db-topic");
        KStream<String, String> sourceLightProcessedStream = sourceLightStream.mapValues(value -> {
            try {
                // Converter o JSON em um mapa
                Map<String, Object> map = new HashMap<>();
                String[] entries = value.replace("{", "").replace("}", "").split(",");
                for (String entry : entries) {
                    String[] keyValue = entry.split(":");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String val = keyValue[1].trim().replace("\"", "");
                        map.put(key, val);
                    }
                }
        
                // Remover o campo "content", se existir
                map.remove("content");
        
                // Reconstruir o JSON a partir do mapa
                StringBuilder result = new StringBuilder("{");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (result.length() > 1) {
                        result.append(",");
                    }
                    result.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
                }
                result.append("}");
        
                return result.toString();
        
            } catch (Exception e) {
                e.printStackTrace(); // Registrar o erro (ou tratar conforme necessário)
                return null; // Retorna null ou um valor padrão em caso de erro
            }
        });        
        sourceLightProcessedStream.to("source-light-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Etapa 4: Processar mensagens de `source-light-topic` e enviar para `light-control-topic`
        KStream<String, String> lightControlStream = builder.stream("source-light-topic");
        lightControlStream.to("light-control-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Criar a instância do Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        System.out.println("Kafka Streams iniciado com sucesso!");

        // Adicionar um gancho para encerramento
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando Kafka Streams...");
            streams.close();
        }));
    }
}
