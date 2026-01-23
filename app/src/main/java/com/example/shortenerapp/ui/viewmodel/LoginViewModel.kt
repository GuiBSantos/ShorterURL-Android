package com.example.shortenerapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.LoginRequest
import com.example.shortenerapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    var forgotPasswordLoading by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
        private set

    var rememberMe by mutableStateOf(true)
    var forgotPasswordStep by mutableStateOf(1)
    var recoveryCode by mutableStateOf("")
    var newRecoveryPassword by mutableStateOf("")


    fun validateCode(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.validateCode(email, recoveryCode)
                if (response.isSuccessful) {
                    forgotPasswordStep = 3
                    onSuccess()
                } else {
                    onError("Código incorreto ou expirado.")
                }
            } catch (e: Exception) {
                onError("Erro de conexão.")
            } finally {
                isLoading = false
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Digite um e-mail válido")
            return
        }

        forgotPasswordLoading = true
        viewModelScope.launch {
            try {
                val response = repository.forgotPassword(email)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Falha ao enviar e-mail. Verifique se o endereço está correto.")
                }
            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            } finally {
                forgotPasswordLoading = false
            }
        }
    }
    fun resetState() {
        isLoading = false
    }
    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {

        if (email.isBlank()) {
            onError("Preencha o e-mail")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Formato de e-mail inválido")
            return
        }
        if (pass.isBlank()) {
            onError("Preencha a senha")
            return
        }

        isLoading = true

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = pass)
                val response = repository.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    repository.saveToken(loginResponse.token)

                    repository.saveRememberMe(rememberMe)

                    onSuccess()
                } else {
                    onError("E-mail ou senha inválidos")
                }
            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun sendRecoveryEmail(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.forgotPassword(email)
                if (response.isSuccessful) {
                    forgotPasswordStep = 2
                    onSuccess()
                } else {
                    onError("Erro ao enviar e-mail.")
                }
            } catch (e: Exception) {
                onError("Erro de conexão.")
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPasswordFinal(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.resetPassword(email, recoveryCode, newRecoveryPassword)
                if (response.isSuccessful) {
                    onSuccess()
                    forgotPasswordStep = 1
                    recoveryCode = ""
                    newRecoveryPassword = ""
                } else {
                    onError("Erro ao redefinir senha.")
                }
            } catch (e: Exception) {
                onError("Erro de conexão.")
            } finally {
                isLoading = false
            }
        }
    }


}