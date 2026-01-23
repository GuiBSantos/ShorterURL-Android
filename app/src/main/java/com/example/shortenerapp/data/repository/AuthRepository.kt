package com.example.shortenerapp.data.repository

import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.model.ChangePasswordRequest
import com.example.shortenerapp.data.model.ForgotPasswordRequest
import com.example.shortenerapp.data.model.LoginRequest
import com.example.shortenerapp.data.model.RegisterRequest
import com.example.shortenerapp.data.model.ResetPasswordRequest
import com.example.shortenerapp.data.model.ValidateCodeRequest
import com.example.shortenerapp.data.network.RetrofitClient
import okhttp3.MultipartBody

class AuthRepository(private val tokenManager: TokenManager) {
    private val api = RetrofitClient.getService(tokenManager)

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

    suspend fun getUserProfile() = api.getUserProfile()

    suspend fun checkUsernameAvailability(username: String): Result<Boolean> {
        return try {
            val response = api.checkUsername(username)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Erro"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun checkEmailAvailability(email: String): Result<Boolean> {
        return try {
            val response = api.checkEmail(email)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Erro"))
        } catch (e: Exception) { Result.failure(e) }
    }

    fun saveRememberMe(value: Boolean) { tokenManager.saveRememberMe(value) }

    suspend fun changePassword(request: ChangePasswordRequest) = api.changePassword(request)
    suspend fun uploadAvatar(body: MultipartBody.Part) = api.uploadAvatar(body)
    suspend fun forgotPassword(email: String) = api.forgotPassword(ForgotPasswordRequest(email))

    suspend fun resetPassword(email: String, code: String, newPass: String) = api.resetPassword(
        ResetPasswordRequest(email, code, newPass)
    )
    suspend fun validateCode(email: String, code: String) =
        api.validateCode(ValidateCodeRequest(email, code))
}