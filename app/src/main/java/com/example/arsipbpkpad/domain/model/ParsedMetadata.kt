package com.example.arsipbpkpad.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ParsedMetadata(
    val docNumber: String? = null,
    val year: Int? = null,
    val subject: String? = null,
    val docType: String? = null,
    val nominal: Double? = null
) : Parcelable
