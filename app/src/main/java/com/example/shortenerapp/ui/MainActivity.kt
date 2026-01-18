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
import com.example.shortenerapp.ui.viewmodel.LoginViewModel
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current

            val tokenManager = remember { TokenManager(context) }
            val authRepository = remember { AuthRepository(tokenManager) }
            val urlRepository = remember { UrlRepository() }


            val loginViewModel = remember { LoginViewModel(authRepository) }
            val shortenerViewModel = remember { ShortenerViewModel(urlRepository) }

            var isLoggedIn by remember { mutableStateOf(tokenManager.getToken() != null) }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    if (isLoggedIn) {
                        EncurtadorScreen(
                            viewModel = shortenerViewModel,
                            onLogout = {
                                authRepository.clearToken()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}