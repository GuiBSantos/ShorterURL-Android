package com.example.shortenerapp.data.repository

import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.LoginRequest
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.network.RetrofitClient

class AuthRepository(private val tokenManager: TokenManager) {
    private val api = RetrofitClient.authService

    suspend fun login(loginRequest: LoginRequest) = api.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = api.register(registerRequest)


    fun saveToken(token: String) {
        tokenManager.saveToken(token)
    }

    fun getToken(): String? {
        return tokenManager.getToken()
    }

    fun clearToken() {
        tokenManager.clearToken()
    }
}