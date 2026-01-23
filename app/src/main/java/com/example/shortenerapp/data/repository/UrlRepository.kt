package com.example.shortenerapp.data.repository

import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.model.ShortenUrlResponse
import com.example.shortenerapp.data.model.UserResponse
import com.example.shortenerapp.data.network.ApiService
import com.example.shortenerapp.data.network.RetrofitClient
import retrofit2.Response

class UrlRepository(private val tokenManager: TokenManager) {

    private val api: ApiService = RetrofitClient.getService(tokenManager)

    suspend fun shortenUrl(request: ShortenUrlRequest) = api.encurtarUrl(request)

    suspend fun deleteUrl(shortCode: String) = api.deletarUrl(shortCode)

    suspend fun getUserHistory(): Result<List<ShortenUrlResponse>> {
        return try {

            val response = api.getMyUrls()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro na API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Response<UserResponse> {
        return api.getUserProfile()
    }
}