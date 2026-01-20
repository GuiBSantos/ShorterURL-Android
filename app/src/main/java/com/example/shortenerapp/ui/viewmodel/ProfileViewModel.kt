package com.example.shortenerapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var username by mutableStateOf("Carregando...")
    var email by mutableStateOf("...")
    var isLoading by mutableStateOf(false)

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    username = user.username
                    email = user.email ?: "Email não informado"
                } else {
                    username = "Erro ao carregar"
                }
            } catch (e: Exception) {
                username = "Sem conexão"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}