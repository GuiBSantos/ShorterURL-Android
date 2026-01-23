package com.example.shortenerapp.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: String,
    val username: String,
    val email: String?,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)

data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)