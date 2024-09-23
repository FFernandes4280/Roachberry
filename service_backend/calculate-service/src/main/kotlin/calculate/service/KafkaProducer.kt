package org.example.calculate.service

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.example.calculate.service.services.MessageService
import java.util.Properties

class KafkaProducer(brokerIp: String, brokerPort: Int) {
    private val producer: KafkaProducer<String, String>

    init {
        val props = Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "$brokerIp:$brokerPort")
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        }
        producer = KafkaProducer(props)
    }

    fun sendMessage(topic: String, message: MessageService) {
        val jsonMessage = Json.encodeToString(message)
        val record = ProducerRecord(topic, message.service, jsonMessage)
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                println("Enviando mensagem para o tópico ${metadata.topic()}, partićão ${metadata.partition()} com valor de offset ${metadata.offset()}\n")
            } else {
                exception.printStackTrace()
            }
        }
    }

    fun close() {
        producer.close()
    }
}