package com.example.shortenerapp.data.network

import com.example.shortenerapp.data.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    Android Studio: http://10.0.2.2:8080/
//    Celular: http://IP:8080/

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}