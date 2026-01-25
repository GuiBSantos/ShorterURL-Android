package com.example.shortenerapp.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)

data class ValidateCodeRequest(
    val email: String,
    val code: String
)

data class UpdateUsernameRequest(
    val newUsername: String
)

data class DeleteAccountRequest(
    val password: String
)