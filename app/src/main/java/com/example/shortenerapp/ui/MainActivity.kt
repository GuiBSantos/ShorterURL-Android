package com.example.shortenerapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.shortenerapp.data.local.TokenManager
import com.example.shortenerapp.data.repository.AuthRepository
import com.example.shortenerapp.data.repository.UrlRepository
import com.example.shortenerapp.ui.screens.EncurtadorScreen
import com.example.shortenerapp.ui.screens.LoginScreen
import com.example.shortenerapp.ui.screens.ProfileScreen
import com.example.shortenerapp.ui.screens.RegisterScreen
import com.example.shortenerapp.ui.viewmodel.LoginViewModel
import com.example.shortenerapp.ui.viewmodel.ProfileViewModel
import com.example.shortenerapp.ui.viewmodel.RegisterViewModel
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel

enum class Screen { LOGIN, REGISTER, HOME, PROFILE }
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current

            val tokenManager = remember { TokenManager(context) }
            val authRepository = remember { AuthRepository(tokenManager) }
            val urlRepository = remember { UrlRepository(tokenManager) }

            val loginViewModel = remember { LoginViewModel(authRepository) }
            val registerViewModel = remember { RegisterViewModel(authRepository) }
            val shortenerViewModel = remember { ShortenerViewModel(urlRepository) }
            val profileViewModel = remember { ProfileViewModel(authRepository, tokenManager) }

            var currentScreen by remember {
                mutableStateOf(if (tokenManager.getToken() != null) Screen.HOME else Screen.LOGIN)
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    when (currentScreen) {
                        Screen.LOGIN -> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { currentScreen = Screen.HOME },
                                onNavigateToRegister = { currentScreen = Screen.REGISTER }
                            )
                        }
                        Screen.REGISTER -> {
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = { currentScreen = Screen.LOGIN },
                                onBackToLogin = { currentScreen = Screen.LOGIN }
                            )
                        }
                        Screen.HOME -> {
                            EncurtadorScreen(
                                viewModel = shortenerViewModel,
                                onNavigateToProfile = { currentScreen = Screen.PROFILE }
                            )
                        }
                        Screen.PROFILE -> {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onBack = { currentScreen = Screen.HOME },
                                onLogout = {
                                    currentScreen = Screen.LOGIN
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}