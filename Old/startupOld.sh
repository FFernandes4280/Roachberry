#!/bin/bash

cd kafka
echo "Starting Zookeeper..."
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > zookeeper.log 2>&1 &
sleep 5
echo "Starting Kafka server..."
nohup bin/kafka-server-start.sh config/server.properties > kafka.log 2>&1 &
sleep 5
echo "Creating topics..."
bin/kafka-topics.sh --create --topic request-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic response-topic --bootstrap-server localhost:9092

bin/kafka-topics.sh --create --topic request-concatena-string-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic response-concatena-string-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic request-salva-arquivo-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic response-salva-arquivo-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic request-calcula-funcao-topic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic response-calcula-funcao-topic --bootstrap-server localhost:9092

cd ..
cd roachberry
# echo "Please run the topic router or add the command to this script..."
