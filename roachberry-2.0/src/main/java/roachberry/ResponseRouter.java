package roachberry;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

public class ResponseRouter {

    public static void main(String[] args) {
        // Configuração do Kafka Streams
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "response-router");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.96.213:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.AT_LEAST_ONCE); // Garantias de processamento

        // Construção da topologia
        StreamsBuilder builder = new StreamsBuilder();

        // Processamento de tópicos de resposta
        KStream<String, String> concatenaStringStream = builder.stream("response-concatena-string-topic");
        concatenaStringStream.to("response-topic", Produced.with(Serdes.String(), Serdes.String()));

        KStream<String, String> salvaArquivoStream = builder.stream("response-salva-arquivo-topic");
        salvaArquivoStream.to("response-topic", Produced.with(Serdes.String(), Serdes.String()));

        KStream<String, String> calculaFuncaoStream = builder.stream("response-calcula-funcao-topic");
        calculaFuncaoStream.to("response-topic", Produced.with(Serdes.String(), Serdes.String()));

        // Criar a instância do Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.setUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("Erro inesperado na thread " + thread.getName() + ": " + exception.getMessage());
        });
        streams.start();

        // Exibir a topologia
        System.out.println("Topologia construída:\n" + streams.toString());

        // Adicionar um gancho de encerramento
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Encerrando Kafka Streams...");
            streams.close();
        }));
    }
}
