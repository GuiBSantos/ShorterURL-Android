package com.example.shortenerapp.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import com.example.shortenerapp.ui.viewmodel.ProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    val context = LocalContext.current

    var isPasswordExpanded by remember { mutableStateOf(false) }
    var showCurrentPass by remember { mutableStateOf(false) }
    var showNewPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
        viewModel.currentPassword = ""
        viewModel.newPassword = ""
        viewModel.confirmNewPassword = ""
        viewModel.deleteAccountPassword = ""
        viewModel.editUsernameText = ""
        viewModel.feedbackMessage = null
        isPasswordExpanded = false
        showCurrentPass = false
        showNewPass = false
        showConfirmPass = false
        viewModel.showEditUsernameDialog = false
        viewModel.showDeleteAccountDialog = false
    }

    val arrowRotation by animateFloatAsState(targetValue = if (isPasswordExpanded) 180f else 0f, label = "arrow")

    val clientId = stringResource(id = R.string.default_web_client_id)
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

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

    val isDark = isDarkTheme
    val bgImageRes = if (isDark) R.drawable.bg_dark_profile else R.drawable.bg_light_profile
    val logoRes = if (isDark) R.drawable.ic_logo_app_dark else R.drawable.ic_logo_app_light
    val textColor = if (isDark) Color.White else Color(0xFF1E293B)
    val subTextColor = if (isDark) Color.White.copy(0.7f) else Color(0xFF64748B)
    val accentColor = Color(0xFF3B82F6)
    val labelColor = if (isDark) Color.White.copy(0.7f) else Color(0xFF64748B)
    val successColor = Color(0xFF4CAF50)
    val dangerColor = Color(0xFFFF5252)
    val cardBackgroundColor = if (isDark) Color.White.copy(0.05f) else Color(0xFFF1F5F9).copy(alpha = 0.95f)
    val cardBorderColor = if (isDark) Color.White.copy(0.1f) else Color(0xFFCBD5E1)

    val glassInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, cursorColor = accentColor, focusedBorderColor = accentColor, unfocusedBorderColor = if(isDark) Color.White.copy(0.3f) else Color(0xFF94A3B8), focusedTextColor = textColor, unfocusedTextColor = textColor, focusedLabelColor = accentColor, unfocusedLabelColor = labelColor
    )
    val glassBrush = Brush.verticalGradient(colors = listOf(if (isDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f), if (isDark) Color(0xFF0F172A).copy(alpha = 0.80f) else Color.White.copy(alpha = 0.90f)))
    val dialogTextColor = if (isDark) Color.White else Color(0xFF1E293B)
    val dialogSubColor = if (isDark) Color.White.copy(0.7f) else Color(0xFF64748B)
    val dialogInputColors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, cursorColor = accentColor, focusedBorderColor = accentColor, unfocusedBorderColor = dialogTextColor.copy(alpha = 0.3f), focusedTextColor = dialogTextColor, unfocusedTextColor = dialogTextColor, focusedLabelColor = accentColor, unfocusedLabelColor = dialogSubColor)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = bgImageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = if(isDark) 0.5f else 0.1f)))

        Box(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onToggleTheme, modifier = Modifier.background(if(isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f), CircleShape).border(1.dp, if(isDark) Color.White.copy(0.1f) else Color.Black.copy(0.1f), CircleShape)) {
                Icon(imageVector = if (isDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay, contentDescription = "Tema", tint = textColor)
            }
        }

        Box(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp), contentAlignment = Alignment.TopStart) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Voltar", tint = textColor) }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        ) {
            val contentWidth = if (maxWidth > 600.dp) 500.dp else maxWidth

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.width(contentWidth).padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(id = logoRes), contentDescription = "Logo", modifier = Modifier.height(55.dp))

                    Spacer(modifier = Modifier.height(20.dp))

                    AvatarSection(viewModel, photoPickerLauncher, isDark, accentColor)
                    Spacer(modifier = Modifier.height(20.dp))
                    UserInfoSection(viewModel, textColor, accentColor, subTextColor)
                    Spacer(modifier = Modifier.height(40.dp))
                    PasswordSection(
                        viewModel, isPasswordExpanded, { isPasswordExpanded = !isPasswordExpanded },
                        showCurrentPass, { showCurrentPass = !showCurrentPass },
                        showNewPass, { showNewPass = !showNewPass },
                        showConfirmPass, { showConfirmPass = !showConfirmPass },
                        arrowRotation, accentColor, labelColor, glassInputColors, successColor, dangerColor, cardBackgroundColor, cardBorderColor
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    ActionButtonsSection(viewModel, googleSignInClient, onLogout, dangerColor, cardBackgroundColor, subTextColor)

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    if (viewModel.showEditUsernameDialog) {
        Dialog(onDismissRequest = { viewModel.showEditUsernameDialog = false }) {
            Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent, modifier = Modifier.fillMaxWidth().border(1.dp, if(isDark) Color.White.copy(0.1f) else Color.Black.copy(0.1f), RoundedCornerShape(24.dp)).padding(16.dp)) {
                Box(modifier = Modifier.background(glassBrush).padding(24.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Alterar Usuário", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = dialogTextColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = viewModel.editUsernameText, onValueChange = { if(it.length <= 20) { viewModel.editUsernameText = it.filter { c -> !c.isWhitespace() } } }, label = { Text("Novo usuário") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = dialogInputColors, shape = RoundedCornerShape(12.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { viewModel.showEditUsernameDialog = false }) { Text("Cancelar", color = dialogSubColor) }
                            Button(onClick = { viewModel.updateUsername(onSuccess = { viewModel.showEditUsernameDialog = false }) }, colors = ButtonDefaults.buttonColors(containerColor = accentColor), shape = RoundedCornerShape(12.dp), enabled = !viewModel.isLoading && viewModel.editUsernameText.isNotBlank()) { if(viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Salvar") }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showDeleteAccountDialog) {
        Dialog(onDismissRequest = { viewModel.showDeleteAccountDialog = false }) {
            Surface(shape = RoundedCornerShape(24.dp), color = Color.Transparent, modifier = Modifier.fillMaxWidth().border(1.dp, dangerColor.copy(0.5f), RoundedCornerShape(24.dp)).padding(16.dp)) {
                Box(modifier = Modifier.background(glassBrush).padding(24.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Warning, null, tint = dangerColor); Spacer(modifier = Modifier.width(8.dp)); Text("Tem certeza?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = dangerColor) }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Digite sua senha para confirmar a exclusão. Todos os seus dados serão apagados para sempre.", color = dialogSubColor, fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(value = viewModel.deleteAccountPassword, onValueChange = { viewModel.deleteAccountPassword = it }, label = { Text("Sua senha atual") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = dangerColor, cursorColor = dangerColor, focusedLabelColor = dangerColor, focusedTextColor = dialogTextColor, unfocusedTextColor = dialogTextColor, unfocusedBorderColor = dialogTextColor.copy(0.3f), unfocusedLabelColor = dialogSubColor), shape = RoundedCornerShape(12.dp))
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { viewModel.showDeleteAccountDialog = false }) { Text("Cancelar", color = dialogSubColor) }
                            Button(onClick = { viewModel.deleteAccount(onSuccess = { viewModel.showDeleteAccountDialog = false; googleSignInClient.signOut().addOnCompleteListener { onLogout() } }) }, colors = ButtonDefaults.buttonColors(containerColor = dangerColor), shape = RoundedCornerShape(12.dp), enabled = !viewModel.isLoading && viewModel.deleteAccountPassword.isNotBlank()) { if(viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Excluir Conta") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarSection(viewModel: ProfileViewModel, launcher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>, isDark: Boolean, accentColor: Color) {
    Box(modifier = Modifier.size(130.dp).clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(120.dp).clip(CircleShape)) {
            if (viewModel.avatarUrl != null) AsyncImage(model = viewModel.avatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            else Box(modifier = Modifier.fillMaxSize().background(if(isDark) Color.White.copy(0.1f) else Color.White), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = if(isDark) Color.White.copy(0.5f) else Color.Gray) }
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset((-4).dp, (-4).dp).size(36.dp).clip(CircleShape).background(Color.Black.copy(0.6f)).border(1.dp, Color.White.copy(0.3f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
        if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(120.dp), color = accentColor)
    }
}

@Composable
fun UserInfoSection(viewModel: ProfileViewModel, textColor: Color, accentColor: Color, subTextColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.editUsernameText = viewModel.username; viewModel.showEditUsernameDialog = true }) {
        Text(viewModel.username, style = TextStyle(fontFamily = ArkhipFont, fontSize = 26.sp, color = textColor))
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Edit, "Editar Nome", tint = accentColor, modifier = Modifier.size(18.dp))
    }
    Text(viewModel.email, fontSize = 16.sp, color = subTextColor)
}

@Composable
fun PasswordSection(
    viewModel: ProfileViewModel, isExpanded: Boolean, onToggle: () -> Unit,
    showCurrent: Boolean, onToggleCurrent: () -> Unit,
    showNew: Boolean, onToggleNew: () -> Unit,
    showConfirm: Boolean, onToggleConfirm: () -> Unit,
    arrowRotation: Float, accentColor: Color, labelColor: Color, inputColors: TextFieldColors, successColor: Color, dangerColor: Color, bgColor: Color, borderColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(16.dp)).border(1.dp, borderColor, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp))) {
        Row(modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Alterar Senha", color = accentColor, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.KeyboardArrowDown, "Expandir", tint = accentColor, modifier = Modifier.rotate(arrowRotation))
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                OutlinedTextField(value = viewModel.currentPassword, onValueChange = { viewModel.currentPassword = it }, label = { Text("Senha Atual") }, visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = onToggleCurrent) { Icon(if (showCurrent) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor) } }, modifier = Modifier.fillMaxWidth(), colors = inputColors, singleLine = true)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = viewModel.newPassword, onValueChange = { viewModel.newPassword = it }, label = { Text("Nova Senha") }, visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = onToggleNew) { Icon(if (showNew) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor) } }, modifier = Modifier.fillMaxWidth(), colors = inputColors, singleLine = true)
                if (viewModel.newPassword.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordValidationItem("Mínimo 8 caracteres", viewModel.newPassword.length >= 8, successColor, labelColor)
                    PasswordValidationItem("Uma letra maiúscula", viewModel.newPassword.any { it.isUpperCase() }, successColor, labelColor)
                    PasswordValidationItem("Um caractere especial (@#!)", viewModel.newPassword.any { !it.isLetterOrDigit() }, successColor, labelColor)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = viewModel.confirmNewPassword, onValueChange = { viewModel.confirmNewPassword = it }, label = { Text("Confirmar Senha") }, visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = onToggleConfirm) { Icon(if (showConfirm) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = labelColor) } }, isError = viewModel.confirmNewPassword.isNotEmpty() && viewModel.confirmNewPassword != viewModel.newPassword, modifier = Modifier.fillMaxWidth(), colors = inputColors, singleLine = true)
                if(viewModel.confirmNewPassword.isNotEmpty() && viewModel.confirmNewPassword != viewModel.newPassword) { Text("Senhas não coincidem", color = dangerColor, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp)) }
                Spacer(modifier = Modifier.height(20.dp))
                val isFormValid = viewModel.newPassword.length >= 8 && viewModel.newPassword.any { it.isUpperCase() } && viewModel.newPassword.any { !it.isLetterOrDigit() } && viewModel.newPassword == viewModel.confirmNewPassword && viewModel.currentPassword.isNotEmpty()
                Button(onClick = { viewModel.changePassword() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = accentColor, disabledContainerColor = accentColor.copy(0.4f)), shape = RoundedCornerShape(12.dp), enabled = !viewModel.isLoading && isFormValid) { if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("ATUALIZAR SENHA", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun ActionButtonsSection(viewModel: ProfileViewModel, googleClient: com.google.android.gms.auth.api.signin.GoogleSignInClient, onLogout: () -> Unit, dangerColor: Color, bgColor: Color, subTextColor: Color) {
    Button(onClick = {
        googleClient.signOut().addOnCompleteListener {
            viewModel.logout()
            onLogout()
        }
    }, modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, dangerColor.copy(0.5f), RoundedCornerShape(16.dp)), colors = ButtonDefaults.buttonColors(containerColor = bgColor), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(0.dp)) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.ExitToApp, null, tint = dangerColor); Spacer(modifier = Modifier.width(12.dp)); Text("Sair da Conta", color = dangerColor, fontWeight = FontWeight.Medium) }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    TextButton(onClick = { viewModel.showDeleteAccountDialog = true }) { Text("Excluir minha conta", color = dangerColor, fontWeight = FontWeight.Bold) }
    Text("Essa ação é irreversível.", color = subTextColor, fontSize = 12.sp)
}

@Composable
fun PasswordValidationItem(text: String, isValid: Boolean, successColor: Color, defaultColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(imageVector = if (isValid) Icons.Outlined.Check else Icons.Outlined.Close, contentDescription = null, tint = if (isValid) successColor else defaultColor, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = if (isValid) successColor else defaultColor, fontSize = 12.sp)
    }
}