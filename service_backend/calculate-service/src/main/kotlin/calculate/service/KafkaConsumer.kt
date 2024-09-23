package org.example.calculate.service

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.example.calculate.service.services.CalculateService
import org.example.calculate.service.services.MessageService
import java.time.Duration
import java.util.Properties

class KafkaConsumer(
    brokerIp: String,
    brokerPort: Int,
    groupId: String,
    private val producer: KafkaProducer,
    private val outputTopic: String
) {
    private val consumer: KafkaConsumer<String, String>
    init {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "$brokerIp:$brokerPort")
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        }
        consumer = KafkaConsumer(props)
    }
    fun subscribe(topic: String) {
        consumer.subscribe(listOf(topic))
    }

    fun pollMessages() {
        while (true) {
            val records: ConsumerRecords<String, String> = consumer.poll(Duration.ofMillis(100))
            for (record in records) {
                try {
                    val message = Json.decodeFromString<MessageService>(record.value())
                    val responseMessage = CalculateService().calculaFuncao(message.body)
                    producer.sendMessage(outputTopic, responseMessage)

                    println("Received message:\n" +
                            "Service: ${message.service},\n" +
                            "Body: ${message.body},\n" +
                            "Offset: ${record.offset()}\n")

                } catch (e: Exception) {
                    println("Failed to decode message: ${record.value()}")
                    e.printStackTrace()
                }


            }
        }
    }

    fun close() {
        consumer.close()
    }
}