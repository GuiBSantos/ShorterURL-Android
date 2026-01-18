package com.example.shortenerapp.data.repository

import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.network.RetrofitClient

class UrlRepository {

    private val api = RetrofitClient.authService

    suspend fun shortenUrl(originalUrl: String) =
        api.encurtarUrl(ShortenUrlRequest(url = originalUrl))

    suspend fun deleteUrl(shortCode: String) =
        api.deletarUrl(shortCode)
}