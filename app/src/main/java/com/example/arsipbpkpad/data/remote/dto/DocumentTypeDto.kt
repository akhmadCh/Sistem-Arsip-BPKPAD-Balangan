package com.example.arsipbpkpad.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentTypeDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("is_system") val isSystem: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_by") val createdBy: String? = null
)
