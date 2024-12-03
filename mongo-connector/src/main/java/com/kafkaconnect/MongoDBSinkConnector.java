package com.kafkaconnect;

import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;
import java.util.Map;

public class MongoDBSinkConnector extends SinkConnector {

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void stop() {
        // Add any necessary cleanup code here
    }

    private String mongoUri;
    private String database;
    private String collection;

    @Override
    public void start(Map<String, String> props) {
        mongoUri = props.get("mongodb.uri");
        database = props.get("mongodb.database");
        collection = props.get("mongodb.collection");
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MongoDBSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return List.of(Map.of(
                "mongodb.uri", mongoUri,
                "mongodb.database", database,
                "mongodb.collection", collection
        ));
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef()
                .define("mongodb.uri", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MongoDB URI")
                .define("mongodb.database", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MongoDB Database")
                .define("mongodb.collection", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MongoDB Collection");
    }

}

