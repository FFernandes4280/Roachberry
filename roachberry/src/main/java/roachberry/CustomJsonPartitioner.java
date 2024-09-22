package roachberry;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import java.util.Map;

public class CustomJsonPartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
        // Configure partitioner if needed (optional)
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // Retrieve the total number of partitions for the given topic
        int numPartitions = cluster.partitionCountForTopic(topic);

        // Custom partitioning logic based on key or value
        if (value.toString().contains("\"identifier\":\"A\"")) {
            return 0 % numPartitions; // Route to partition 0
        } else if (value.toString().contains("\"identifier\":\"B\"")) {
            return 1 % numPartitions; // Route to partition 1
        } else if (value.toString().contains("\"identifier\":\"C\"")) {
            return 2 % numPartitions; // Route to partition 2
        } else {
            return 3 % numPartitions; // Route to partition 3
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
}
