#!/bin/bash

# Start Zookeeper in the background with nohup and log the output
cd kafka
echo "Starting Zookeeper..."
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > zookeeper.log 2>&1 &

# Give Zookeeper a few seconds to start
sleep 5

# Start Kafka server in the background with nohup and log the output
echo "Starting Kafka server..."
nohup bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &

nohup bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic streams-input

nohup bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 3 \
    --topic streams-output \
    --config cleanup.policy=compact

cd ..

cd roachberry

echo "Running the pipiline"
nohup mvn clean package
nohup mvn exec:java -Dexec.mainClass=myapps.Pipe
