package com.kafkaconnect;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;

public class MongoDBSinkTask extends SinkTask {

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
    private MongoDatabase mongoDatabase;
    private MongoCollection<org.bson.Document> mongoCollection;

    @Override
    public void start(Map<String, String> props) {
        mongoUri = props.get("mongodb.uri");
        database = props.get("mongodb.database");
        collection = props.get("mongodb.collection");

        mongoDatabase = MongoClients.create(mongoUri).getDatabase(database);
        mongoCollection = mongoDatabase.getCollection(collection);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
            Struct value = (Struct) record.value();
            org.bson.Document document = new org.bson.Document();

            value.schema().fields().forEach(field -> {
                document.put(field.name(), value.get(field));
            });

            mongoCollection.insertOne(document);
        }
    }
}
