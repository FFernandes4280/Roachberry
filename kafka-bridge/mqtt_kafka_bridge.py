import paho.mqtt.client as mqttClient
from kafka import KafkaProducer, KafkaConsumer

# Configuration
MQTT_BROKER = '200.235.84.122'
MQTT_TOPIC_MQTT_TO_KAFKA = 'qr-code-read-topic'
MQTT_TOPIC_KAFKA_TO_MQTT = 'action-topic'
KAFKA_BROKER = '200.235.84.122:9092'
KAFKA_TOPIC_MQTT_TO_KAFKA = 'qr-code-content-topic'
KAFKA_TOPIC_KAFKA_TO_MQTT = 'light-control-topic'

# Kafka Producer
kafka_producer = KafkaProducer(bootstrap_servers=KAFKA_BROKER)

# Kafka Consumer
kafka_consumer = KafkaConsumer(
    KAFKA_TOPIC_KAFKA_TO_MQTT,
    bootstrap_servers=KAFKA_BROKER,
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id='mqtt_to_kafka_group'
)

# MQTT Callbacks
def on_connect(client, userdata, flags, rc):
    print(f"Connected to MQTT Broker with result code {rc}")
    client.subscribe(MQTT_TOPIC_MQTT_TO_KAFKA)

def on_message(client, userdata, msg):
    print(f"Received message from MQTT: {msg.payload.decode()}")
    # Forward to Kafka
    kafka_producer.send(KAFKA_TOPIC_MQTT_TO_KAFKA, msg.payload)

# MQTT Client
mqtt_client = mqttClient.Client()
mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message
mqtt_client.connect(MQTT_BROKER)

# Kafka-to-MQTT Publisher
def kafka_to_mqtt_publisher():
    for message in kafka_consumer:
        print(f"Received message from Kafka: {message.value.decode()}")
        mqtt_client.publish(MQTT_TOPIC_KAFKA_TO_MQTT, message.value)

# Main loop
if __name__ == "__main__":
    from threading import Thread

    # Start Kafka-to-MQTT publisher in a separate thread
    kafka_thread = Thread(target=kafka_to_mqtt_publisher)
    kafka_thread.start()

    # Start MQTT loop
    mqtt_client.loop_forever()
