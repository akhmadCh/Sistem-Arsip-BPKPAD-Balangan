package com.example.arsipbpkpad.domain.usecase

import com.example.arsipbpkpad.core.common.ResultState
import com.example.arsipbpkpad.domain.model.ParsedMetadata
import com.example.arsipbpkpad.domain.repository.AiParserRepository
import javax.inject.Inject

class ParseMetadataWithAiUseCase @Inject constructor(
    private val aiParserRepository: AiParserRepository
) {
    suspend operator fun invoke(rawText: String): ResultState<ParsedMetadata> {
        return aiParserRepository.parseMetadata(rawText)
    }
}
