package com.example.qrcodeview

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.qrcodeview.ui.theme.QRCodeViewTheme
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRCodeViewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val qrContent = result.contents
                val json = createJson(qrContent)
                sendMqttMessage(json)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun createJson(content: String): String {
        val qrData = QRData(source = "mobile", light = "pisca", content = content)
        return Json.encodeToString(qrData)
    }

    private fun sendMqttMessage(message: String) {
        val brokerUrl = "tcp://broker.hivemq.com:1883"
        val topic = "kafka/connect/topic"
        val client = MqttClient(brokerUrl, MqttClient.generateClientId())
        client.connect()
        val mqttMessage = MqttMessage(message.toByteArray())
        client.publish(topic, mqttMessage)
        client.disconnect()
    }
}

@Serializable
data class QRData(val source: String, val light: String, val content: String)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QRCodeViewTheme {
        Greeting("Android")
    }
}