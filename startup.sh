#!/bin/bash

cd kafka
echo "Starting Zookeeper..."
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > zookeeper.log 2>&1 &

# Give Zookeeper a few seconds to start
sleep 5

# Start Kafka server in the background with nohup and log the output
echo "Starting Kafka server..."
nohup bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &

sleep 5

#Creating topicos
echo "Creating topics..."
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic source-topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic topic-A
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic topic-B
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic topic-C
cd ..

cd roachberry

echo "Please run the topic router or add the command to this script..."
