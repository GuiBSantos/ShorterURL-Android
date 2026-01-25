package com.example.shortenerapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }

    val context = LocalContext.current

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

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
            painter = painterResource(id = bgImageRes), contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Box(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp), contentAlignment = Alignment.TopEnd) {
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.background(Color.White.copy(0.1f), CircleShape).border(1.dp, Color.White.copy(0.1f), CircleShape)
            ) {
                Icon(imageVector = if (isDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay, contentDescription = "Tema", tint = textColor)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bem-vindo", style = TextStyle(fontFamily = ArkhipFont, fontSize = 40.sp, color = textColor))
            Text("Faça login com seu e-mail", color = labelColor)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { input ->
                    if (input.length <= 100) {
                        email = input.filter { !it.isWhitespace() }
                        errorMessage = null
                    }
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
                onValueChange = { input ->
                    if (input.length <= 64) {
                        password = input
                        errorMessage = null
                    }
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
                    onCheckedChange = { isChecked -> viewModel.rememberMe = isChecked },
                    colors = CheckboxDefaults.colors(checkedColor = accentColor, uncheckedColor = labelColor, checkmarkColor = Color.White)
                )
                Text("Lembrar-se de mim", color = labelColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = { showForgotDialog = true }) {
                    Text("Esqueceu a senha?", color = labelColor, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = errorColor, style = TextStyle(fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 12.dp))
            }

            Button(
                onClick = { viewModel.login(email, password, onSuccess = onLoginSuccess, onError = { msg -> errorMessage = msg }) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = email.isNotEmpty() && password.isNotEmpty() && !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("ENTRAR", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Não tem conta? Registre-se", color = accentColor, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showForgotDialog) {
        val glassGradient = Brush.verticalGradient(
            colors = listOf(
                if (isDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f),
                if (isDark) Color(0xFF0F172A).copy(alpha = 0.80f) else Color.White.copy(alpha = 0.85f)
            )
        )
        val glassBorder = if (isDark) Color.White.copy(0.1f) else Color.White
        val textColorDialog = if (isDark) Color.White else Color(0xFF1E293B)
        val subTextColor = if (isDark) Color.White.copy(0.7f) else Color.Gray

        val dialogInputColors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = accentColor,
            focusedBorderColor = accentColor,
            unfocusedBorderColor = if(isDark) Color.White.copy(0.2f) else Color.Black.copy(0.2f),
            focusedTextColor = textColorDialog,
            unfocusedTextColor = textColorDialog,
            focusedLabelColor = accentColor,
            unfocusedLabelColor = subTextColor
        )

        Dialog(onDismissRequest = {
            showForgotDialog = false
            viewModel.forgotPasswordStep = 1
            viewModel.recoveryCode = ""
            viewModel.newRecoveryPassword = ""
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, glassBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(glassGradient)
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = when (viewModel.forgotPasswordStep) {
                                1 -> "Recuperar Senha"
                                2 -> "Verificar Código"
                                else -> "Criar Nova Senha"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColorDialog
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (viewModel.forgotPasswordStep) {
                                1 -> "Digite seu e-mail para receber o código."
                                2 -> "Enviamos um código para $forgotEmail"
                                else -> "Crie uma nova senha segura."
                            },
                            fontSize = 14.sp,
                            color = subTextColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        when (viewModel.forgotPasswordStep) {
                            1 -> {
                                OutlinedTextField(
                                    value = forgotEmail,
                                    onValueChange = { input ->
                                        if (input.length <= 100) forgotEmail = input.filter { !it.isWhitespace() }
                                    },
                                    label = { Text("E-mail") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = dialogInputColors,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                )
                            }
                            2 -> {
                                OutlinedTextField(
                                    value = viewModel.recoveryCode,
                                    onValueChange = {
                                        if (it.length <= 6 && it.all { c -> c.isDigit() }) viewModel.recoveryCode = it
                                    },
                                    label = { Text("Código (6 dígitos)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = dialogInputColors,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                            3 -> {
                                OutlinedTextField(
                                    value = viewModel.newRecoveryPassword,
                                    onValueChange = { input ->
                                        if (input.length <= 64) viewModel.newRecoveryPassword = input
                                    },
                                    label = { Text("Nova Senha") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = dialogInputColors,
                                    shape = RoundedCornerShape(12.dp),
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                when (viewModel.forgotPasswordStep) {
                                    1 -> viewModel.sendRecoveryEmail(forgotEmail,
                                        onSuccess = { Toast.makeText(context, "Código enviado!", Toast.LENGTH_SHORT).show() },
                                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })

                                    2 -> viewModel.validateCode(forgotEmail,
                                        onSuccess = { Toast.makeText(context, "Código verificado!", Toast.LENGTH_SHORT).show() },
                                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })

                                    3 -> viewModel.resetPasswordFinal(forgotEmail,
                                        onSuccess = {
                                            Toast.makeText(context, "Senha alterada!", Toast.LENGTH_LONG).show()
                                            showForgotDialog = false
                                        },
                                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            enabled = !viewModel.isLoading && (
                                    (viewModel.forgotPasswordStep == 1 && forgotEmail.isNotEmpty()) ||
                                            (viewModel.forgotPasswordStep == 2 && viewModel.recoveryCode.length == 6) ||
                                            (viewModel.forgotPasswordStep == 3 && viewModel.newRecoveryPassword.length >= 8)
                                    )
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = when (viewModel.forgotPasswordStep) {
                                        1 -> "Enviar Código"
                                        2 -> "Validar Código"
                                        else -> "Confirmar Alteração"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(onClick = {
                            showForgotDialog = false
                            viewModel.forgotPasswordStep = 1
                        }) {
                            Text("Cancelar", color = subTextColor)
                        }
                    }
                }
            }
        }
    }
}