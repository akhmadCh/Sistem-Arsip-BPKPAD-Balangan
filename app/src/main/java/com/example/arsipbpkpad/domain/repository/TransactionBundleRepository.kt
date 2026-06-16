package com.example.arsipbpkpad.domain.repository

import com.example.arsipbpkpad.core.common.ResultState

interface TransactionBundleRepository {
    suspend fun createBundle(
        description: String?,
        documentType: String,
        year: Int
    ): ResultState<String>
}
