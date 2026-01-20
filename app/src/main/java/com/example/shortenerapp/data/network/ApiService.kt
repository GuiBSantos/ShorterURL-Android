package com.example.shortenerapp.data.network

import com.example.shortenerapp.data.model.LoginRequest
import com.example.shortenerapp.data.model.LoginResponse
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.model.ShortenUrlResponse
import com.example.shortenerapp.data.model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @POST("api/shorten")
    suspend fun encurtarUrl(@Body request: ShortenUrlRequest): Response<ShortenUrlResponse>

    @DELETE("api/urls/{shortCode}")
    suspend fun deletarUrl(@Path("shortCode") shortCode: String): Response<Void>

    @GET("api/my-urls")
    suspend fun getMyUrls(): Response<List<ShortenUrlResponse>>

    @GET("auth/me")
    suspend fun getUserProfile(): Response<UserResponse>
}