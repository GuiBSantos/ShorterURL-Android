package com.example.shortenerapp.data.repository

import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.network.RetrofitClient

class UrlRepository(tokenManager: TokenManager) {

    private val api = RetrofitClient.getService(tokenManager)

    suspend fun deleteUrl(shortCode: String) =
        api.deletarUrl(shortCode)

    suspend fun shortenUrl(request: ShortenUrlRequest) =
        api.encurtarUrl(request)

    suspend fun getMyUrls() = api.getMyUrls()
}