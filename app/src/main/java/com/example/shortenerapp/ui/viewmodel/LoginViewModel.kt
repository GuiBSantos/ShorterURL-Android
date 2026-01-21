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

    var isLoading by mutableStateOf(false)
        private set

    var rememberMe by mutableStateOf(true)

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
}