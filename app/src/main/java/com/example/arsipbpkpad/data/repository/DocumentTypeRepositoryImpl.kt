package com.example.arsipbpkpad.data.repository

import com.example.arsipbpkpad.data.mapper.toDomain
import com.example.arsipbpkpad.data.mapper.toDto
import com.example.arsipbpkpad.data.remote.dto.DocumentTypeDto
import com.example.arsipbpkpad.data.util.safeApiCall
import com.example.arsipbpkpad.domain.model.DocumentType
import com.example.arsipbpkpad.domain.model.DocumentTypeDefaults.normalizeDocumentType
import com.example.arsipbpkpad.domain.model.DomainResult
import com.example.arsipbpkpad.domain.repository.AuthRepository
import com.example.arsipbpkpad.domain.repository.DocumentTypeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named

class DocumentTypeRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository,
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher
) : DocumentTypeRepository {

    private val tableName = "document_types"
    private val _documentTypes = MutableStateFlow<List<DocumentType>>(emptyList())

    override suspend fun getActiveDocumentTypes(): DomainResult<List<DocumentType>> {
        return safeApiCall(ioDispatcher) {
            val response = supabaseClient.postgrest[tableName]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("is_active", true)
                    }
                }
                .decodeList<DocumentTypeDto>()
            
            val domainList = response.map { it.toDomain() }
            _documentTypes.value = domainList
            domainList
        }
    }

    override suspend fun ensureDocumentTypeExists(type: String): DomainResult<Unit> {
        val normalized = normalizeDocumentType(type)
        if (normalized.isBlank()) return DomainResult.Error("Jenis dokumen tidak boleh kosong")

        return safeApiCall(ioDispatcher) {
            // Check if already exists in local cache first
            if (_documentTypes.value.any { it.code == normalized }) {
                return@safeApiCall
            }

            // Check remote
            val existing = supabaseClient.postgrest[tableName]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("code", normalized)
                    }
                }
                .decodeSingleOrNull<DocumentTypeDto>()

            if (existing == null) {
                val userId = authRepository.getCurrentUserId()
                val newDocType = DocumentType(
                    code = normalized,
                    name = normalized,
                    isSystem = false,
                    isActive = true
                )
                supabaseClient.postgrest[tableName].insert(newDocType.toDto(userId))
                // Refresh local cache
                getActiveDocumentTypes()
            }
        }
    }

    override fun observeDocumentTypes(): Flow<List<DocumentType>> = _documentTypes.asStateFlow()
}
