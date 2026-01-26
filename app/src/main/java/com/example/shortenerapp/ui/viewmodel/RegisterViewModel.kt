package com.example.shortenerapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.repository.AuthRepository
import com.example.shortenerapp.ui.utils.ErrorUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private var usernameCheckJob: Job? = null
    private var emailCheckJob: Job? = null

    var isCheckingUsername by mutableStateOf(false)
    var isCheckingEmail by mutableStateOf(false)

    var usernameError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var generalError by mutableStateOf<String?>(null)
        private set

    fun onGoogleRegister(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.googleLogin(idToken)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    repository.saveToken(loginResponse.token)
                    repository.saveRememberMe(true)

                    onSuccess()
                } else {
                    onError("Falha ao registrar com Google.")
                }
            } catch (e: Exception) {
                onError(ErrorUtils.parseError(e))
            } finally {
                isLoading = false
            }
        }
    }

    fun onUsernameChange(newUsername: String) {

        usernameCheckJob?.cancel()
        usernameError = null

        usernameCheckJob = viewModelScope.launch {

            delay(600)

            if (newUsername.length >= 3) {
                isCheckingUsername = true
                println("DEBUG: Verificando usuário: $newUsername")

                val result = repository.checkUsernameAvailability(newUsername)

                result.onSuccess { exists ->
                    println("DEBUG: Resposta usuário '$newUsername'. Existe? $exists")
                    if (exists) {
                        usernameError = "Este usuário já está em uso"
                    }
                }.onFailure { e ->
                    println("DEBUG: Erro ao verificar usuário: ${e.message}")
                }

                isCheckingUsername = false
            }
        }
    }

    fun onEmailChange(newEmail: String) {
        emailCheckJob?.cancel()
        emailError = null

        if (!newEmail.contains("@") || newEmail.length < 5) return

        emailCheckJob = viewModelScope.launch {
            delay(600)

            isCheckingEmail = true
            println("DEBUG: Verificando email: $newEmail")

            val result = repository.checkEmailAvailability(newEmail)

            result.onSuccess { exists ->
                println("DEBUG: Resposta email '$newEmail'. Existe? $exists")
                if (exists) {
                    emailError = "Este e-mail já está cadastrado"
                }
            }.onFailure { e ->
                println("DEBUG: Erro ao verificar email: ${e.message}")
            }

            isCheckingEmail = false
        }
    }

    fun resetState() {
        usernameError = null
        emailError = null
        generalError = null
        isLoading = false
        isCheckingUsername = false
        isCheckingEmail = false
    }

    fun clearErrors() {
        generalError = null
    }

    private fun validarSenha(senha: String): String? {
        if (senha.length < 8) return "A senha deve ter no mínimo 8 caracteres."
        if (!senha.any { it.isUpperCase() }) return "A senha deve ter pelo menos uma letra MAIÚSCULA."
        if (!senha.any { it.isLowerCase() }) return "A senha deve ter pelo menos uma letra minúscula."
        if (!senha.any { !it.isLetterOrDigit() }) return "A senha deve ter pelo menos um caractere especial (@, #, !, etc)."
        return null
    }

    fun register(username: String, email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        clearErrors()

        if (usernameError != null || emailError != null) {
            return
        }

        if (username.isBlank()) {
            usernameError = "Obrigatório"
            return
        }
        if (email.isBlank()) {
            emailError = "Obrigatório"
            return
        }
        if (!email.contains("@")) {
            emailError = "E-mail inválido"
            return
        }

        if (pass.isBlank()) {
            generalError = "A senha não pode estar vazia"
            onError(generalError!!)
            return
        }

        val erroSenha = validarSenha(pass)
        if (erroSenha != null) {
            generalError = erroSenha
            onError(erroSenha)
            return
        }

        isLoading = true

        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, email, pass)
                val response = repository.register(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val rawError = response.errorBody()?.string()?.lowercase() ?: ""

                    if (rawError.contains("username") || rawError.contains("usuário")) {
                        usernameError = "Este usuário já existe"
                    } else if (rawError.contains("email") || rawError.contains("e-mail")) {
                        emailError = "Este e-mail já está em uso"
                    } else {
                        generalError = "Erro no cadastro."
                        onError("Erro no cadastro (Cod: ${response.code()})")
                    }
                }
            } catch (e: Exception) {
                generalError = ErrorUtils.parseError(e)
                onError(generalError!!)
            } finally {
                isLoading = false
            }
        }
    }
}