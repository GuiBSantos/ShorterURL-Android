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

    var username by mutableStateOf("") // TODO: alterar
    var password by mutableStateOf("") // TODO: alterar
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun onLoginClick() {
        if (username.isBlank() || password.isBlank()) {
            loginError = "Preencha todos os campos"
            return
        }

        isLoading = true
        loginError = null

        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = repository.login(request)

                if (response.isSuccessful && response.body() != null) {

                    val token = response.body()!!.token
                    repository.saveToken(token)
                    loginSuccess = true
                } else {
                    loginError = "Erro: Usuário ou senha inválidos"
                }
            } catch (e: Exception) {
                loginError = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}