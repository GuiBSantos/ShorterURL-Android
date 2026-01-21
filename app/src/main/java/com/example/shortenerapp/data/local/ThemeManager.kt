package com.example.shortenerapp.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_DARK = "is_dark_theme"
    }

    private val _isDarkTheme = MutableStateFlow(isDarkThemeSaved())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun isDarkThemeSaved(): Boolean {
        return prefs.getBoolean(KEY_IS_DARK, isSystemInDarkMode())
    }

    fun toggleTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
        _isDarkTheme.value = isDark
    }

    private fun isSystemInDarkMode(): Boolean {
        val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }
}