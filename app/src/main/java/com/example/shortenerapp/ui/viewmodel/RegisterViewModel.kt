package com.example.shortenerapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var registerError by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    private fun validarSenha(senha: String): String? {
        if (senha.length < 8) return "A senha deve ter no mínimo 8 caracteres."
        if (!senha.any { it.isUpperCase() }) return "A senha deve ter pelo menos uma letra MAIÚSCULA."
        if (!senha.any { it.isLowerCase() }) return "A senha deve ter pelo menos uma letra minúscula."
        if (!senha.any { !it.isLetterOrDigit() }) return "A senha deve ter pelo menos um caractere especial (@, #, !, etc)."
        return null
    }

    fun resetState() {
        username = ""
        email = ""
        password = ""
        registerError = null
        registerSuccess = false
        isLoading = false
    }
    fun onRegisterClick() {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            registerError = "Preencha todos os campos"
            return
        }

        val erroSenha = validarSenha(password)
        if (erroSenha != null) {
            registerError = erroSenha
            return
        }

        isLoading = true
        registerError = null

        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                val response = repository.register(request)

                if (response.isSuccessful) {
                    registerSuccess = true
                    println("Sucesso no cadastro!")
                } else {

                    val errorMsg = response.errorBody()?.string() ?: "Erro desconhecido"
                    registerError = "Falha: $errorMsg (Cod: ${response.code()})"
                }
            } catch (e: Exception) {
                registerError = "Erro de conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}