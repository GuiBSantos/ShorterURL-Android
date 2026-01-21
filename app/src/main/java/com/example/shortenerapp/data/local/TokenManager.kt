package com.example.shortenerapp.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT = "jwt_token"
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_JWT, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_JWT).apply()
    }

    fun saveRememberMe(remember: Boolean) {
        prefs.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
    }

    fun isRememberMe(): Boolean {

        return prefs.getBoolean(KEY_REMEMBER_ME, true)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}