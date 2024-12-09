package com.example.lighton

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var mqttClient: MqttAndroidClient
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    private lateinit var sourceTextView: TextView
    private lateinit var lightTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceTextView = findViewById(R.id.sourceTextView)
        lightTextView = findViewById(R.id.lightTextView)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        val serverUri = "tcp://<BROKER_IP>:1883"
        val clientId = "AndroidClient"
        mqttClient = MqttAndroidClient(applicationContext, serverUri, clientId)

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                // Handle connection lost
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                message?.let {
                    val payload = it.toString()
                    val jsonObject = JSONObject(payload)
                    val source = jsonObject.getString("Source")
                    val light = jsonObject.getString("Light")

                    runOnUiThread {
                        sourceTextView.text = "Source: $source"
                        lightTextView.text = "Light: $light"
                    }

                    when (light) {
                        "acende" -> toggleFlashlight(true)
                        "apaga" -> toggleFlashlight(false)
                        "pisca" -> blinkFlashlight()
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Handle delivery complete
            }
        })

        mqttClient.connect(null, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                mqttClient.subscribe("<TOPIC>", 1)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // Handle failure
            }
        })
    }

    private fun toggleFlashlight(status: Boolean) {
        cameraId?.let {
            cameraManager.setTorchMode(it, status)
        }
    }

    private fun blinkFlashlight() {
        cameraId?.let {
            for (i in 1..3) {
                cameraManager.setTorchMode(it, true)
                Thread.sleep(500)
                cameraManager.setTorchMode(it, false)
                Thread.sleep(500)
            }
        }
    }
}