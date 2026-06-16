package com.example.arsipbpkpad.domain.repository

import com.example.arsipbpkpad.core.common.ResultState
import com.example.arsipbpkpad.domain.model.ParsedMetadata

interface AiParserRepository {
    suspend fun parseMetadata(rawText: String): ResultState<ParsedMetadata>
}
