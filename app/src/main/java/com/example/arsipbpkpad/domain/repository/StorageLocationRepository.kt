package com.example.arsipbpkpad.domain.repository

import com.example.arsipbpkpad.core.common.ResultState

interface StorageLocationRepository {
    suspend fun getOrCreateLocation(
        room: String,
        shelf: String,
        boxNumber: String,
        year: String
    ): ResultState<String>
}
