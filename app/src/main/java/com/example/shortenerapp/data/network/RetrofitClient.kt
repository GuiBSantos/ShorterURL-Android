package com.example.shortenerapp.data.network

import com.example.shortenerapp.data.local.TokenManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    Android Studio: http://10.0.2.2:8080/
//    Celular: http://IP:8080/

    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun getService(tokenManager: TokenManager): ApiService {

        val authInterceptor = AuthInterceptor(tokenManager)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}