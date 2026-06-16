package com.example.arsipbpkpad.data.repository

import com.example.arsipbpkpad.core.common.ResultState
import com.example.arsipbpkpad.domain.repository.TransactionBundleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class TransactionBundleDto(
    @SerialName("id") val id: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("document_type") val documentType: String,
    @SerialName("year") val year: Int
)

class TransactionBundleRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient
) : TransactionBundleRepository {

    override suspend fun createBundle(
        description: String?,
        documentType: String,
        year: Int
    ): ResultState<String> {
        return try {
            val bundle = TransactionBundleDto(
                description = description,
                documentType = documentType,
                year = year
            )

            val inserted = supabaseClient.postgrest["transaction_bundles"]
                .insert(bundle) {
                    select()
                }
                .decodeSingle<TransactionBundleDto>()

            ResultState.Success(inserted.id!!)
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Failed to create transaction bundle")
        }
    }
}
