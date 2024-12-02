#!/bin/bash

cd kafka
echo "Starting Zookeeper..."
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > zookeeper.log 2>&1 &
sleep 5
echo "Starting Kafka server..."
nohup bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &
sleep 5
echo "Creating topics..."
#Kafka Cluster
bin/kafka-topics.sh --create --topic source-db-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic source-ligh-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic qrcode-content-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic save-data-db-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic light-control-topic --bootstrap-server localhost:9092
#Kafka Connect Cluster

cd ..
cd roachberry
# echo "Please run the topic router or add the command to this script..."
