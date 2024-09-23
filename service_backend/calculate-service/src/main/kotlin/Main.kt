package org.example

import org.example.calculate.service.KafkaConsumer
import org.example.calculate.service.KafkaProducer

fun main() {
    val brokerIp = "192.168.96.213"
    val brokerPort = 9092
    val inputTopic = "request-calcula-funcao-topic"
    val outputTopic = "response-calcula-funcao-topic"
    val groupId = "calculate-consumer-group"

    // Produtor Kafka
    val producer = KafkaProducer(brokerIp, brokerPort)

    // Consumidor Kafka
    val consumer = KafkaConsumer(brokerIp, brokerPort, groupId, producer, outputTopic)

    consumer.subscribe(inputTopic)
    consumer.pollMessages()
    consumer.close()
    producer.close()
}