package com.example.shortenerapp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.shortenerapp.R
import com.example.shortenerapp.ui.theme.ArkhipFont
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel

@Composable
fun EncurtadorScreen(
    viewModel: ShortenerViewModel,
    onNavigateToProfile: () -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    // Estados locais
    var urlDigitada by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var inputCliques by remember { mutableStateOf("") }
    var inputTempo by remember { mutableStateOf("") }
    var unidadeTempo by remember { mutableStateOf("Minutos") }
    val unidades = listOf("Minutos", "Horas", "Dias")

    var mostrarDialogoHistorico by remember { mutableStateOf(false) }
    var mostrarDialogoShare by remember { mutableStateOf(false) }
    var mensagemPersonalizada by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        viewModel.carregarPerfilUsuario()
        viewModel.carregarHistorico()
        urlDigitada = ""
        resultado = ""
        inputCliques = ""
        inputTempo = ""
    }

    val isAppDark = isDarkTheme

    val bgImageRes = if (isAppDark) R.drawable.bg_dark_main else R.drawable.bg_light_main
    val logoRes = if (isAppDark) R.drawable.ic_logo_app_dark else R.drawable.ic_logo_app_light

    val textColor = Color.White
    val labelColor = textColor.copy(alpha = 0.7f)
    val contrastButtonColor = if (isAppDark) Color(0xFF64FFDA) else Color.White

    val transparentInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        cursorColor = if (isAppDark) contrastButtonColor else textColor,
        focusedBorderColor = if (isAppDark) contrastButtonColor else textColor,
        unfocusedBorderColor = textColor.copy(alpha = 0.5f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = if (isAppDark) contrastButtonColor else textColor,
        unfocusedLabelColor = labelColor,
        focusedPlaceholderColor = labelColor,
        unfocusedPlaceholderColor = labelColor
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (isAppDark) 0.4f else 0.2f))
        )

        // --- CONTEÚDO PRINCIPAL ---
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // Em telas grandes, limita a largura a 600dp para ficar centralizado e bonito
            val contentWidth = if (maxWidth > 600.dp) 600.dp else maxWidth

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Fixo no topo da coluna)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(Color.White.copy(0.1f), CircleShape)
                            .border(1.dp, Color.White.copy(0.1f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isAppDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay,
                            contentDescription = "Tema",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Image(
                        painter = painterResource(id = logoRes),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(55.dp)
                    )

                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            viewModel.carregarHistorico()
                            mostrarDialogoHistorico = true
                        }) {
                            Icon(Icons.Default.History, null, tint = textColor)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(onClick = onNavigateToProfile) {
                            val avatarUrl = viewModel.userAvatarUrl.value
                            if (avatarUrl != null) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(Icons.Default.Person, null, tint = textColor)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Simplifique seus links",
                    color = textColor.copy(0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = ArkhipFont
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- COLUNA CENTRALIZADA (Inputs e Botões) ---
                Column(
                    modifier = Modifier.width(contentWidth),
                    verticalArrangement = Arrangement.spacedBy(24.dp) // Espaçamento maior entre blocos
                ) {
                    // Inputs
                    InputsSection(
                        urlDigitada, { urlDigitada = it },
                        inputCliques, { if (it.all { c -> c.isDigit() }) inputCliques = it },
                        inputTempo, { if (it.all { c -> c.isDigit() }) inputTempo = it },
                        unidadeTempo, { unidadeTempo = it },
                        unidades,
                        transparentInputColors, labelColor, contrastButtonColor, textColor
                    )

                    // Ação (Botão Encurtar e Resultado)
                    ActionSection(
                        urlDigitada, inputCliques, inputTempo, unidadeTempo,
                        viewModel,
                        resultado, { resultado = it },
                        context, contrastButtonColor, labelColor, textColor, clipboardManager,
                        { mensagemPersonalizada = ""; mostrarDialogoShare = true }
                    )
                }
            }
        }

        if (mostrarDialogoHistorico) {
            DialogHistoricoContent(isAppDark, viewModel, clipboardManager, context) {
                mostrarDialogoHistorico = false
            }
        }

        if (mostrarDialogoShare) {
            DialogShareContent(
                isAppDark,
                mensagemPersonalizada,
                { mensagemPersonalizada = it },
                resultado,
                context
            ) { mostrarDialogoShare = false }
        }
    }
}

// --- SUB-COMPONENTES (Podem ser copiados do código anterior ou mantidos aqui) ---

@Composable
fun InputsSection(
    url: String, onUrlChange: (String) -> Unit,
    cliques: String, onCliquesChange: (String) -> Unit,
    tempo: String, onTempoChange: (String) -> Unit,
    unidade: String, onUnidadeChange: (String) -> Unit,
    unidades: List<String>,
    colors: TextFieldColors, labelColor: Color, contrastColor: Color, textColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Cole sua URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = colors,
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Link, null, tint = labelColor) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = cliques,
                onValueChange = onCliquesChange,
                label = { Text("Max. Acessos") },
                modifier = Modifier.weight(1f),
                colors = colors,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = tempo,
                onValueChange = onTempoChange,
                label = { Text("Tempo") },
                modifier = Modifier.weight(1f),
                colors = colors,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        if (tempo.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                unidades.forEach { u ->
                    val isSelected = u == unidade
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isSelected) contrastColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) contrastColor else textColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { onUnidadeChange(u) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = u,
                            color = if (isSelected) contrastColor else textColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionSection(
    url: String, cliques: String, tempo: String, unidade: String,
    viewModel: ShortenerViewModel,
    resultado: String, onResultadoChange: (String) -> Unit,
    context: android.content.Context,
    contrastColor: Color, labelColor: Color, textColor: Color,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    onShare: () -> Unit
) {
    val isButtonEnabled = url.isNotEmpty()
    Button(
        onClick = {
            val maxClicks = cliques.toIntOrNull()
            val tempoVal = tempo.toLongOrNull()
            val expMinutes = if (tempoVal != null) {
                when (unidade) {
                    "Minutos" -> tempoVal
                    "Horas" -> tempoVal * 60
                    "Dias" -> tempoVal * 1440
                    else -> tempoVal
                }
            } else null
            viewModel.encurtarUrl(
                url,
                maxClicks,
                expMinutes,
                { onResultadoChange(it) },
                { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                2.dp,
                if (isButtonEnabled) contrastColor else labelColor.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = isButtonEnabled
    ) {
        if (viewModel.isLoadingEncurtar.value) {
            CircularProgressIndicator(color = contrastColor, modifier = Modifier.size(24.dp))
        } else {
            Text(
                "Encurtar URL",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isButtonEnabled) contrastColor else labelColor.copy(alpha = 0.5f)
            )
        }
    }

    if (resultado.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(0.1f))
                .border(1.dp, contrastColor.copy(0.5f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = resultado,
                    modifier = Modifier.weight(1f),
                    color = contrastColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(resultado))
                    Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
                }) { Icon(Icons.Default.ContentCopy, null, tint = textColor) }
                IconButton(onClick = onShare) { Icon(Icons.Default.Share, null, tint = textColor) }
            }
        }
    }
}

// ... DialogHistoricoContent e DialogShareContent ...
// (Mantenha os dialogs que você já tem no código anterior, eles não precisam mudar)
@Composable
fun DialogHistoricoContent(
    isAppDark: Boolean,
    viewModel: ShortenerViewModel,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    context: android.content.Context,
    onDismiss: () -> Unit
) {
    val glassGradient = Brush.verticalGradient(
        colors = listOf(
            if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.98f),
            if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.90f)
        )
    )
    val glassBorder = if (isAppDark) Color.White.copy(0.1f) else Color(0xFFCBD5E1)

    val itemGradient = Brush.horizontalGradient(
        colors = if (isAppDark) {
            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
        } else {
            listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        }
    )

    val itemBorder = if (isAppDark) Color.White.copy(0.05f) else Color(0xFFCBD5E1)
    val itemTitleColor = if (isAppDark) Color(0xFFC7D2FE) else Color(0xFF2563EB)
    val itemSubColor = if (isAppDark) Color.White.copy(0.6f) else Color(0xFF64748B)
    val itemIconColor = if (isAppDark) Color.White.copy(0.7f) else Color(0xFF64748B)
    val deleteIconColor = if (isAppDark) Color(0xFFFF8A80) else Color(0xFFEF4444)
    val mainTitleColor = if (isAppDark) Color.White else Color(0xFF334155)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, glassBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .background(glassGradient)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Histórico Recente",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = mainTitleColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(modifier = Modifier.heightIn(max = 400.dp)) {
                        if (viewModel.isLoadingHistorico.value) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = itemTitleColor)
                            }
                        } else if (viewModel.historico.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum histórico encontrado.", color = itemSubColor)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(viewModel.historico) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(itemGradient)
                                            .border(1.dp, itemBorder, RoundedCornerShape(16.dp))
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.encurtada,
                                                color = itemTitleColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = item.original,
                                                color = itemSubColor,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Expira: ${item.dataExpiracao}",
                                                color = itemSubColor.copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(item.encurtada))
                                                    Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT)
                                                        .show()
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.ContentCopy,
                                                    null,
                                                    tint = itemIconColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    viewModel.deletarUrl(
                                                        item,
                                                        {},
                                                        {})
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    null,
                                                    tint = deleteIconColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemGradient)
                            .clickable { onDismiss() }
                            .border(1.dp, itemBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Fechar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = itemTitleColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogShareContent(
    isAppDark: Boolean,
    mensagem: String,
    onMensagemChange: (String) -> Unit,
    resultado: String,
    context: android.content.Context,
    onDismiss: () -> Unit
) {
    val glassGradient = Brush.verticalGradient(
        colors = listOf(
            if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.98f),
            if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.90f)
        )
    )
    val glassBorder = if (isAppDark) Color.White.copy(0.1f) else Color(0xFFCBD5E1)
    val dialogTextColor = if (isAppDark) Color.White else Color(0xFF1E293B)
    val dialogSubColor = if (isAppDark) Color.White.copy(0.7f) else Color(0xFF64748B)
    val primaryColor = if (isAppDark) Color(0xFF64FFDA) else Color(0xFF3B82F6)

    val dialogInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = primaryColor,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = dialogTextColor.copy(alpha = 0.3f),
        focusedTextColor = dialogTextColor,
        unfocusedTextColor = dialogTextColor,
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = dialogSubColor
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, glassBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent
        ) {
            Box(modifier = Modifier.background(glassGradient).padding(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isAppDark) primaryColor.copy(0.1f) else Color(0xFFEFF6FF),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Share, null, tint = primaryColor)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Compartilhar Link",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = dialogTextColor
                    )
                    Text(
                        "Adicione uma mensagem (opcional)",
                        fontSize = 14.sp,
                        color = dialogSubColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = mensagem,
                        onValueChange = onMensagemChange,
                        label = { Text("Mensagem") },
                        placeholder = { Text("Ex: Olha esse link!") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = dialogInputColors,
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (mensagem.isNotEmpty()) {
                        Text(
                            "Pré-visualização:",
                            fontSize = 12.sp,
                            color = dialogSubColor,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isAppDark) Color.Black.copy(0.2f) else Color.White.copy(0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, glassBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "$mensagem\n$resultado",
                                color = dialogTextColor,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = dialogSubColor)
                        }
                        Button(
                            onClick = {
                                val textoFinal = if (mensagem.isNotBlank()) {
                                    "$mensagem\n\n$resultado"
                                } else {
                                    resultado
                                }

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, textoFinal)
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        sendIntent,
                                        "Compartilhar via"
                                    )
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Compartilhar",
                                color = if (isAppDark) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}