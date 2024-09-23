package roachberry;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

public class ResponseRouter {

    public static void main(String[] args) {
        // Kafka Streams configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "response-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.96.213:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Build the topology
        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> concatena_string = builder.stream("response-concatena-string-topic");
        concatena_string.to(Serdes.String(),Serdes.String(),"response-topic");

        KStream<String, String> salva_arquivo = builder.stream("response-salva-arquivo-topic");
        salva_arquivo.to(Serdes.String(),Serdes.String(),"response-topic");

        KStream<String, String> calcula_funcao = builder.stream("response-calcula-funcao-topic");
        calcula_funcao.to(Serdes.String(),Serdes.String(),"response-topic");

        // Build the Kafka Streams topology
        KafkaStreams streams = new KafkaStreams(builder, props);
        streams.start();

        System.out.println(streams.toString());
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
