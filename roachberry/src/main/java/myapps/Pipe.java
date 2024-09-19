package myapps;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Pipe {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //broker IP
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream("streams-input").to("streams-output"); //redirect the input to the output file


        //Não funciona ainda
        // builder.stream("streams-input")
        //        .map((key, value) -> {
        //        if (value != null && !value.isEmpty()) {
        //            char firstChar = value.charAt(0);
        //            if (firstChar == 'A') {
        //            return new KeyValue<>(key, value);
        //            } else if (firstChar == 'B') {
        //            return new KeyValue<>(key, value);
        //            }
        //        }
        //        return new KeyValue<>(key, value);
        //        })
        //        .to((key, value, recordContext) -> {
        //        if (value != null && !value.isEmpty()) {
        //            char firstChar = value.charAt(0);
        //            if (firstChar == 'A') {
        //            return "streams-output-0";
        //            } else if (firstChar == 'B') {
        //            return "streams-output-1";
        //            }
        //        }
        //        return "streams-output";
        //        })
        //        ;
        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}