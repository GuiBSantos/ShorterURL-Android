package com.example.shortenerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import com.example.shortenerapp.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    onToggleTheme: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = isSystemDark || MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgImageRes = if (isDark) R.drawable.bg_dark_register else R.drawable.bg_light_register

    val textColor = Color.White
    val labelColor = Color.White.copy(alpha = 0.7f)
    val accentColor = Color(0xFF3B82F6)
    val errorColor = Color(0xFFFF5252)
    val successColor = Color(0xFF4CAF50)

    val glassInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = accentColor,
        focusedBorderColor = accentColor,
        unfocusedBorderColor = textColor.copy(alpha = 0.5f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = accentColor,
        unfocusedLabelColor = labelColor,
        errorBorderColor = errorColor,
        errorLabelColor = errorColor,
        errorCursorColor = errorColor,
        errorSupportingTextColor = errorColor,
        errorTextColor = textColor
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .background(Color.White.copy(0.1f), CircleShape)
                    .border(1.dp, Color.White.copy(0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay,
                    contentDescription = "Tema",
                    tint = textColor
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Nova Conta", style = TextStyle(fontFamily = ArkhipFont, fontSize = 36.sp, color = textColor))
            Text("Junte-se a nós hoje", color = labelColor)

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    viewModel.onUsernameChange(it)
                },
                label = { Text("Usuário") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = labelColor) },
                trailingIcon = {
                    if (viewModel.isCheckingUsername) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = accentColor)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = glassInputColors,
                shape = RoundedCornerShape(12.dp),
                isError = viewModel.usernameError != null,
                supportingText = {
                    if (viewModel.usernameError != null) Text(viewModel.usernameError!!)
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.onEmailChange(it)
                },
                label = { Text("E-mail") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = labelColor) },
                trailingIcon = {
                    if (viewModel.isCheckingEmail) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = accentColor)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = glassInputColors,
                shape = RoundedCornerShape(12.dp),
                isError = viewModel.emailError != null,
                supportingText = {
                    if (viewModel.emailError != null) Text(viewModel.emailError!!)
                }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearErrors() },
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
                shape = RoundedCornerShape(12.dp)
            )

            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                    PasswordRequirementRow("Mínimo 8 caracteres", password.length >= 8, successColor, labelColor)
                    PasswordRequirementRow("Uma letra maiúscula", password.any { it.isUpperCase() }, successColor, labelColor)
                    PasswordRequirementRow("Um caractere especial (@#!)", password.any { !it.isLetterOrDigit() }, successColor, labelColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.generalError != null) {
                Text(text = viewModel.generalError!!, color = errorColor, modifier = Modifier.padding(bottom = 8.dp))
            }

            Button(
                onClick = { viewModel.register(username, email, password, onRegisterSuccess, {}) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("CRIAR CONTA", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin) {
                Text("Já tem uma conta? Entrar", color = accentColor)
            }
        }
    }
}

@Composable
fun PasswordRequirementRow(text: String, isValid: Boolean, successColor: Color, defaultColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isValid) Icons.Outlined.Check else Icons.Outlined.Close,
            contentDescription = null,
            tint = if (isValid) successColor else defaultColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (isValid) successColor else defaultColor,
            fontSize = 12.sp
        )
    }
}