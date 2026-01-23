package com.example.shortenerapp.data.model

data class ShortenUrlResponse(
    val url: String,
    val shortCode: String,
    val shortUrl: String,
    val expiresAt: Any? = null
)

data class ShortenUrlRequest(
    val url: String,
    val maxClicks: Int? = null,
    val expirationTimeInMinutes: Long? = null
)

