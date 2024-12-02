package org.example.calculate.service.services

import kotlinx.serialization.Serializable

@Serializable
data class MessageService(val service: String, val body: String)