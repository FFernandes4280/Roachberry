import paho.mqtt.client as mqtt
from kafka import KafkaProducer

MQTT_BROKER = 'localhost'
MQTT_TOPIC = 'kafka'
KAFKA_BROKER = 'localhost:9092'
KAFKA_TOPIC = 'mosquitto'

# Kafka Producer
kafka_producer = KafkaProducer(bootstrap_servers=KAFKA_BROKER)

# MQTT Callbacks
def on_connect(client, userdata, flags, rc):
    print(f"Connected to MQTT Broker with result code {rc}")
    client.subscribe(MQTT_TOPIC)

def on_message(client, userdata, msg):
    print(f"Received message from MQTT: {msg.payload.decode()}")
    # Forward to Kafka
    kafka_producer.send(KAFKA_TOPIC, msg.payload)

# MQTT Client
mqtt_client = mqtt.Client()
mqtt_client.on_connect = on_connect
mqtt_client.on_message = on_message

# Connect to MQTT
mqtt_client.connect(MQTT_BROKER)

# Start loop to process messages
mqtt_client.loop_forever()
