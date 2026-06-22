package com.example.arsipbpkpad.data.repository

import com.example.arsipbpkpad.domain.model.DomainResult
import com.example.arsipbpkpad.domain.model.UserRole
import com.example.arsipbpkpad.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {

    private val _currentUserRole = MutableStateFlow(UserRole.UNKNOWN)
    override val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    override val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    init {
        // Check session on initialization
        checkSessionSync()
    }

    override suspend fun login(email: String, password: String, rememberMe: Boolean): DomainResult<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val role = mapEmailToRole(email)
            _currentUserRole.value = role
            _isUserLoggedIn.value = true
            
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            val message = when (e) {
                is UnknownHostException, is HttpRequestTimeoutException -> "Koneksi internet bermasalah. Silakan periksa jaringan Anda."
                is ResponseException -> {
                    if (e.response.status.value == 400) "Email atau password salah."
                    else "Terjadi kesalahan pada server (${e.response.status.value})."
                }
                else -> e.localizedMessage ?: "Gagal masuk. Silakan coba lagi."
            }
            DomainResult.Error(message)
        }
    }

    override suspend fun logout(): DomainResult<Unit> {
        return try {
            supabase.auth.signOut()
            _currentUserRole.value = UserRole.UNKNOWN
            _isUserLoggedIn.value = false
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(e.localizedMessage ?: "Logout failed")
        }
    }

    override suspend fun checkSession(): Boolean {
        return checkSessionSync()
    }

    private fun checkSessionSync(): Boolean {
        val session = supabase.auth.currentSessionOrNull()
        val user = supabase.auth.currentUserOrNull()
        
        return if (session != null && user != null) {
            val email = user.email ?: ""
            _currentUserRole.value = mapEmailToRole(email)
            _isUserLoggedIn.value = true
            true
        } else {
            _currentUserRole.value = UserRole.UNKNOWN
            _isUserLoggedIn.value = false
            false
        }
    }

    private fun mapEmailToRole(email: String): UserRole {
        return when (email.lowercase()) {
            "kassubag@balangankab.go.id" -> UserRole.KASSUBAG
            "admin123@balangankab.go.id" -> UserRole.ARSIPARIS
            else -> UserRole.UNKNOWN
        }
    }
}
