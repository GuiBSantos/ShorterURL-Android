package com.example.shortenerapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.local.ThemeManager
import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.ChangePasswordRequest
import com.example.shortenerapp.data.repository.AuthRepository
import com.example.shortenerapp.ui.utils.ErrorUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val themeManager: ThemeManager
) : ViewModel() {

    var username by mutableStateOf("Carregando...")
    var email by mutableStateOf("...")
    var avatarUrl by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
    var isDarkTheme by mutableStateOf(false)
    var feedbackMessage by mutableStateOf<String?>(null)

    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmNewPassword by mutableStateOf("")

    var showEditUsernameDialog by mutableStateOf(false)
    var showDeleteAccountDialog by mutableStateOf(false)
    var editUsernameText by mutableStateOf("")
    var deleteAccountPassword by mutableStateOf("")

    init {
        observeTheme()
    }

    private fun observeTheme() {
        viewModelScope.launch {
            themeManager.isDarkTheme.collect { isDark -> isDarkTheme = isDark }
        }
    }

    fun toggleTheme(isChecked: Boolean) {
        viewModelScope.launch { themeManager.toggleTheme(isChecked) }
    }

    fun clearState() {
        username = "Carregando..."
        email = "..."
        avatarUrl = null
        isLoading = false
        feedbackMessage = null
        currentPassword = ""
        newPassword = ""
        confirmNewPassword = ""
        showEditUsernameDialog = false
        showDeleteAccountDialog = false
        editUsernameText = ""
        deleteAccountPassword = ""
    }

    fun logout() {
        tokenManager.clearToken()
        clearState()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    username = user.username
                    email = user.email ?: ""
                    avatarUrl = if (user.avatarUrl != null) "${user.avatarUrl}?t=${System.currentTimeMillis()}" else null
                }
            } catch (e: Exception) {
                feedbackMessage = ErrorUtils.parseError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        val file = uriToFile(context, uri)
        if (file == null) {
            feedbackMessage = "Erro ao ler a imagem."
            return
        }

        val sizeInMb = file.length() / (1024 * 1024)
        if (sizeInMb > 5) {
            feedbackMessage = "Imagem muito grande (Máx 5MB)."
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = repository.uploadAvatar(body)
                if (response.isSuccessful && response.body() != null) {
                    val rawUrl = response.body()!!.avatarUrl
                    avatarUrl = "$rawUrl?t=${System.currentTimeMillis()}"
                    feedbackMessage = "Foto atualizada com sucesso!"
                } else {
                    feedbackMessage = "Falha no upload."
                }
            } catch (e: Exception) {
                feedbackMessage = ErrorUtils.parseError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword() {
        if (newPassword != confirmNewPassword) {
            feedbackMessage = "As senhas não coincidem."
            return
        }
        if (newPassword.length < 8) {
            feedbackMessage = "Mínimo de 8 caracteres."
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val request = ChangePasswordRequest(currentPassword, newPassword)
                val response = repository.changePassword(request)
                if (response.isSuccessful) {
                    feedbackMessage = "Senha alterada!"
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                } else {
                    feedbackMessage = "Senha atual incorreta."
                }
            } catch (e: Exception) {
                feedbackMessage = ErrorUtils.parseError(e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val myFile = File(context.cacheDir, "temp_avatar.jpg")
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(myFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            myFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateUsername(onSuccess: () -> Unit) {
        if (editUsernameText.length < 3) {
            feedbackMessage = "Mínimo 3 caracteres."
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.updateUsername(editUsernameText)
                if (response.isSuccessful) {
                    username = editUsernameText
                    feedbackMessage = "Nome de usuário alterado!"
                    onSuccess()
                } else {
                    feedbackMessage = "Nome já está em uso ou inválido."
                }
            } catch (e: Exception) {
                feedbackMessage = ErrorUtils.parseError(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.deleteAccount(deleteAccountPassword)
                if (response.isSuccessful) {
                    tokenManager.clearToken()
                    clearState()
                    onSuccess()
                } else {
                    feedbackMessage = "Senha incorreta."
                }
            } catch (e: Exception) {
                feedbackMessage = ErrorUtils.parseError(e)
            } finally {
                isLoading = false
            }
        }
    }
}