package com.example.shortenerapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import com.example.shortenerapp.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current

    var isPasswordExpanded by remember { mutableStateOf(false) }
    var showCurrentPass by remember { mutableStateOf(false) }
    var showNewPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(targetValue = if (isPasswordExpanded) 180f else 0f, label = "arrow")

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(viewModel.feedbackMessage) {
        viewModel.feedbackMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.feedbackMessage = null
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.uploadAvatar(context, uri) }
    )

    val isSystemDark = isSystemInDarkTheme()
    val isDark = isSystemDark || MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bgImageRes = if (isDark) R.drawable.bg_dark_profile else R.drawable.bg_light_profile
    val textColor = Color.White
    val accentColor = Color(0xFF3B82F6)
    val labelColor = Color.White.copy(alpha = 0.7f)
    val successColor = Color(0xFF4CAF50)

    val glassInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = accentColor,
        focusedBorderColor = accentColor,
        unfocusedBorderColor = textColor.copy(alpha = 0.3f),
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
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Voltar", tint = textColor)
                }
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

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(120.dp).clip(CircleShape)) {
                    if (viewModel.avatarUrl != null) {
                        AsyncImage(model = viewModel.avatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = textColor.copy(0.5f))
                        }
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).offset((-4).dp, (-4).dp).size(36.dp).clip(CircleShape)
                        .background(Color.Black.copy(0.5f)).border(1.dp, Color.White.copy(0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White.copy(0.9f), modifier = Modifier.size(18.dp))
                }
                if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(120.dp), color = accentColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(viewModel.username, style = TextStyle(fontFamily = ArkhipFont, fontSize = 26.sp, color = textColor))
            Text(viewModel.email, fontSize = 16.sp, color = textColor.copy(0.7f))

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(0.05f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPasswordExpanded = !isPasswordExpanded }
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Alterar Senha", color = accentColor, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.KeyboardArrowDown, "Expandir", tint = accentColor, modifier = Modifier.rotate(arrowRotation))
                }

                AnimatedVisibility(visible = isPasswordExpanded) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {

                        OutlinedTextField(
                            value = viewModel.currentPassword, onValueChange = { viewModel.currentPassword = it },
                            label = { Text("Senha Atual") },
                            visualTransformation = if (showCurrentPass) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showCurrentPass = !showCurrentPass }) {
                                    Icon(if (showCurrentPass) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(), colors = glassInputColors, singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = viewModel.newPassword, onValueChange = { viewModel.newPassword = it },
                            label = { Text("Nova Senha") },
                            visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showNewPass = !showNewPass }) {
                                    Icon(if (showNewPass) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(), colors = glassInputColors, singleLine = true
                        )

                        if (viewModel.newPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            PasswordValidationItem("Mínimo 8 caracteres", viewModel.newPassword.length >= 8, successColor, labelColor)
                            PasswordValidationItem("Uma letra maiúscula", viewModel.newPassword.any { it.isUpperCase() }, successColor, labelColor)
                            PasswordValidationItem("Um caractere especial (@#!)", viewModel.newPassword.any { !it.isLetterOrDigit() }, successColor, labelColor)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = viewModel.confirmNewPassword, onValueChange = { viewModel.confirmNewPassword = it },
                            label = { Text("Confirmar Senha") },
                            visualTransformation = if (showConfirmPass) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPass = !showConfirmPass }) {
                                    Icon(if (showConfirmPass) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor)
                                }
                            },
                            isError = viewModel.confirmNewPassword.isNotEmpty() && viewModel.confirmNewPassword != viewModel.newPassword,
                            modifier = Modifier.fillMaxWidth(), colors = glassInputColors, singleLine = true
                        )
                        if(viewModel.confirmNewPassword.isNotEmpty() && viewModel.confirmNewPassword != viewModel.newPassword) {
                            Text("Senhas não coincidem", color = Color(0xFFFF5252), fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val isFormValid = viewModel.newPassword.length >= 8 &&
                                viewModel.newPassword.any { it.isUpperCase() } &&
                                viewModel.newPassword.any { !it.isLetterOrDigit() } &&
                                viewModel.newPassword == viewModel.confirmNewPassword &&
                                viewModel.currentPassword.isNotEmpty()

                        Button(
                            onClick = { viewModel.changePassword() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor, disabledContainerColor = accentColor.copy(0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !viewModel.isLoading && isFormValid
                        ) {
                            Text("ATUALIZAR SENHA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(0.8f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PasswordValidationItem(text: String, isValid: Boolean, successColor: Color, defaultColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isValid) Icons.Outlined.Check else Icons.Outlined.Close,
            contentDescription = null,
            tint = if (isValid) successColor else defaultColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = if (isValid) successColor else defaultColor, fontSize = 12.sp)
    }
}