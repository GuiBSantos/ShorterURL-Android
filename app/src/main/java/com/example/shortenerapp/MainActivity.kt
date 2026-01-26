package com.example.shortenerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.shortenerapp.data.local.ThemeManager
import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.repository.AuthRepository
import com.example.shortenerapp.data.repository.UrlRepository
import com.example.shortenerapp.ui.screens.EncurtadorScreen
import com.example.shortenerapp.ui.screens.LoginScreen
import com.example.shortenerapp.ui.screens.ProfileScreen
import com.example.shortenerapp.ui.screens.RegisterScreen
import com.example.shortenerapp.ui.screens.Screen
import com.example.shortenerapp.ui.viewmodel.LoginViewModel
import com.example.shortenerapp.ui.viewmodel.ProfileViewModel
import com.example.shortenerapp.ui.viewmodel.RegisterViewModel
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val themeManager = ThemeManager(this)

        if (!tokenManager.isRememberMe()) {
            tokenManager.clearToken()
        }

        val startScreen = if (tokenManager.getToken() != null) Screen.HOME else Screen.LOGIN

        val initialThemeIsDark = themeManager.isDarkThemeSaved()

        setContent {
            val context = LocalContext.current

            val authRepository = remember { AuthRepository(tokenManager) }
            val urlRepository = remember { UrlRepository(tokenManager) }

            val loginViewModel = remember { LoginViewModel(authRepository) }
            val registerViewModel = remember { RegisterViewModel(authRepository) }
            val shortenerViewModel = remember { ShortenerViewModel(urlRepository) }
            val profileViewModel =
                remember { ProfileViewModel(authRepository, tokenManager, themeManager) }

            val isDarkTheme by themeManager.isDarkTheme.collectAsState(initial = initialThemeIsDark)

            val onToggleTheme = {
                themeManager.toggleTheme(!isDarkTheme)
            }

            MaterialTheme(
                colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
            ) {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var currentScreen by remember { mutableStateOf(startScreen) }

                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                loginViewModel.resetState()
                                currentScreen = Screen.HOME
                            },
                            onNavigateToRegister = {
                                loginViewModel.resetState()
                                registerViewModel.resetState()
                                currentScreen = Screen.REGISTER
                            },
                            onToggleTheme = onToggleTheme
                        )

                        Screen.REGISTER -> RegisterScreen(
                            viewModel = registerViewModel,
                            onRegisterSuccess = {
                                registerViewModel.resetState()
                                currentScreen = Screen.LOGIN
                            },
                            onBackToLogin = {
                                registerViewModel.resetState()
                                currentScreen = Screen.LOGIN
                            },
                            onToggleTheme = onToggleTheme
                        )

                        Screen.HOME -> EncurtadorScreen(
                            viewModel = shortenerViewModel,
                            onNavigateToProfile = { currentScreen = Screen.PROFILE },
                            onToggleTheme = onToggleTheme
                        )

                        Screen.PROFILE -> ProfileScreen(
                            viewModel = profileViewModel,
                            onBack = { currentScreen = Screen.HOME },
                            onLogout = {
                                profileViewModel.logout()
                                loginViewModel.resetState()
                                currentScreen = Screen.LOGIN
                            },
                            onToggleTheme = onToggleTheme
                        )
                    }
                }
            }
        }
    }
}