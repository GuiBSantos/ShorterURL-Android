package com.example.shortenerapp.data.network

import com.example.shortenerapp.data.model.ChangePasswordRequest
import com.example.shortenerapp.data.model.ForgotPasswordRequest
import com.example.shortenerapp.data.model.LoginRequest
import com.example.shortenerapp.data.model.LoginResponse
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.model.ResetPasswordRequest
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.model.ShortenUrlResponse
import com.example.shortenerapp.data.model.UserResponse
import com.example.shortenerapp.data.model.ValidateCodeRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("auth/check-username/{username}")
    suspend fun checkUsername(@Path("username") username: String): Response<Boolean>

    @GET("auth/check-email")
    suspend fun checkEmail(@Query("value") email: String): Response<Boolean>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Void>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(@Part image: MultipartBody.Part): Response<UserResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body req: ResetPasswordRequest): Response<Void>

    @POST("auth/validate-code")
    suspend fun validateCode(@Body req: ValidateCodeRequest): Response<Void>

}