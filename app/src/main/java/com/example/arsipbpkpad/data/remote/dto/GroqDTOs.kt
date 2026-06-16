package com.example.arsipbpkpad.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.0,
    @SerialName("max_tokens") val maxTokens: Int = 1024,
    @SerialName("response_format") val responseFormat: GroqResponseFormat? = null
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponseFormat(
    val type: String
)

@Serializable
data class GroqResponse(
    val id: String,
    val choices: List<GroqChoice>
)

@Serializable
data class GroqChoice(
    val message: GroqMessage,
    @SerialName("finish_reason") val finishReason: String
)
