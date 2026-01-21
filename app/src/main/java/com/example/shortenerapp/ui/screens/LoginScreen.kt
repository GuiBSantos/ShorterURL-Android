package com.example.shortenerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import com.example.shortenerapp.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onToggleTheme: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = isSystemDark || MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgImageRes = if (isDark) R.drawable.bg_dark_login else R.drawable.bg_light_login

    val textColor = Color.White
    val labelColor = Color.White.copy(alpha = 0.7f)
    val accentColor = Color(0xFF3B82F6)
    val errorColor = Color(0xFFFF5252)

    val glassInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = accentColor,
        focusedBorderColor = accentColor,
        unfocusedBorderColor = textColor.copy(alpha = 0.5f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = accentColor,
        unfocusedLabelColor = labelColor
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .background(Color.White.copy(0.1f), CircleShape)
                        .border(1.dp, Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay,
                        contentDescription = "Tema",
                        tint = if(isDark) Color.Yellow else textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Bem-vindo", style = TextStyle(fontFamily = ArkhipFont, fontSize = 40.sp, color = textColor))
            Text("Faça login com seu e-mail", color = labelColor)

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = labelColor) },
                modifier = Modifier.fillMaxWidth(),
                colors = glassInputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("Senha") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = labelColor) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = glassInputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.rememberMe,
                    onCheckedChange = { isChecked ->
                        viewModel.rememberMe = isChecked },
                    colors = CheckboxDefaults.colors(
                        checkedColor = accentColor,
                        uncheckedColor = labelColor,
                        checkmarkColor = Color.White
                    )
                )
                Text("Lembrar-se de mim", color = labelColor, fontSize = 14.sp)

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = { /* Lógica de esqueci senha */ }) {
                    Text("Esqueceu a senha?", color = labelColor, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = errorColor,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = {

                    viewModel.login(
                        email,
                        password,
                        onSuccess = onLoginSuccess,
                        onError = { msg -> errorMessage = msg }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ENTRAR", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Não tem conta? Registre-se", color = accentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}