package com.mqtt.kafka.connect;

import org.apache.kafka.connect.source.SourceConnector;

import org.apache.kafka.connect.source.SourceTask;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;

public class MqttSourceConnector extends SourceConnector {
    @Override
    public Class<? extends SourceTask> taskClass() {
        // Return the task implementation class
        return MqttSourceTask.class;
    }
    @Override 
    public void start(Map<String, String> props) {
        // Initialize MQTT broker connection
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        // Provide configurations for tasks
        return Collections.singletonList(new HashMap<>());
    }

    @Override
    public void stop() {
        // Clean up
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef()
                .define("mqtt.server.uri", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "MQTT Broker URI");
    }

    @Override
    public String version() {
        return "1.0";
    }
}

