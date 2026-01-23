package com.example.shortenerapp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    onToggleTheme: () -> Unit
) {
    var urlDigitada by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var inputCliques by remember { mutableStateOf("") }
    var inputTempo by remember { mutableStateOf("") }
    var unidadeTempo by remember { mutableStateOf("Minutos") }
    val unidades = listOf("Minutos", "Horas", "Dias")

    var mostrarDialogoHistorico by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isAppDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val bgImageRes = if (isAppDark) R.drawable.bg_dark_main else R.drawable.bg_light_main

    val textColor = Color.White
    val labelColor = textColor.copy(alpha = 0.7f)
    val accentColor = Color(0xFF3B82F6)

    val contrastButtonColor = if (isAppDark) {
        Color(0xFF64FFDA)
    } else {
        Color.White
    }

    val transparentInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        cursorColor = if(isAppDark) contrastButtonColor else textColor,
        focusedBorderColor = if(isAppDark) contrastButtonColor else textColor,
        unfocusedBorderColor = textColor.copy(alpha = 0.5f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = if(isAppDark) contrastButtonColor else textColor,
        unfocusedLabelColor = labelColor,
        focusedPlaceholderColor = labelColor,
        unfocusedPlaceholderColor = labelColor
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = bgImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if(isAppDark) 0.4f else 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .background(Color.White.copy(0.1f), CircleShape)
                        .border(1.dp, Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isAppDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay,
                        contentDescription = "Tema",
                        tint = textColor
                    )
                }

                Row {
                    IconButton(onClick = {
                        viewModel.carregarHistorico()
                        mostrarDialogoHistorico = true
                    }) {
                        Icon(Icons.Default.History, null, tint = textColor)
                    }

                    IconButton(onClick = onNavigateToProfile) {
                        val avatarUrl = viewModel.userAvatarUrl.value
                        if (avatarUrl != null) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = textColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Text(
                text = "Shortener",
                style = TextStyle(
                    fontFamily = ArkhipFont,
                    fontSize = 42.sp,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(0.5f), blurRadius = 10f
                    )
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Simplifique seus links", color = labelColor, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(40.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = urlDigitada,
                    onValueChange = { urlDigitada = it },
                    label = { Text("Cole sua URL longa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = transparentInputColors,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Link, null, tint = labelColor) }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputCliques,
                        onValueChange = { if (it.all { c -> c.isDigit() }) inputCliques = it },
                        label = { Text("Max. Cliques") },
                        modifier = Modifier.weight(1f),
                        colors = transparentInputColors,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = inputTempo,
                        onValueChange = { if (it.all { c -> c.isDigit() }) inputTempo = it },
                        label = { Text("Tempo") },
                        modifier = Modifier.weight(1f),
                        colors = transparentInputColors,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                if (inputTempo.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        unidades.forEach { unidade ->
                            val isSelected = unidade == unidadeTempo
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (isSelected) contrastButtonColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) contrastButtonColor else textColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { unidadeTempo = unidade }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = unidade,
                                    color = if(isSelected) contrastButtonColor else textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val isButtonEnabled = urlDigitada.isNotEmpty()

                Button(
                    onClick = {
                        val maxClicksFinal = inputCliques.toIntOrNull()
                        val tempoRaw = inputTempo.toLongOrNull()
                        val expirationMinutes = if (tempoRaw != null) {
                            when (unidadeTempo) {
                                "Minutos" -> tempoRaw
                                "Horas" -> tempoRaw * 60
                                "Dias" -> tempoRaw * 1440
                                else -> tempoRaw
                            }
                        } else null
                        viewModel.encurtarUrl(urlDigitada, maxClicksFinal, expirationMinutes, { res -> resultado = res }, {})
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(
                            width = 2.dp,
                            color = if (isButtonEnabled) contrastButtonColor else labelColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        text = "Encurtar Link",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isButtonEnabled) contrastButtonColor else labelColor.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.7f))

            if (resultado.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(0.1f))
                        .border(1.dp, contrastButtonColor.copy(0.5f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = resultado,
                            modifier = Modifier.weight(1f),
                            color = contrastButtonColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(resultado))
                            Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
                        }) { Icon(Icons.Default.ContentCopy, null, tint = textColor) }
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, resultado)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share"))
                        }) { Icon(Icons.Default.Share, null, tint = textColor) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (mostrarDialogoHistorico) {

            val glassGradient = Brush.verticalGradient(
                colors = listOf(
                    if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.70f) else Color.White.copy(alpha = 0.85f),
                    if (isAppDark) Color(0xFF0F172A).copy(alpha = 0.40f) else Color.White.copy(alpha = 0.50f)
                )
            )
            val glassBorder = if (isAppDark) Color.White.copy(0.1f) else Color(0xFFF1F5F9)

            val itemGradient = Brush.horizontalGradient(
                colors = if (isAppDark) {
                    listOf(Color(0xFF0F172A), Color(0xFF312E81))
                } else {
                    listOf(Color(0xFFFFFFFF), Color(0xFFF0F7FF))
                }
            )

            val itemTitleColor = if (isAppDark) Color(0xFFC7D2FE) else Color(0xFF2563EB)
            val itemSubColor   = if (isAppDark) Color.White.copy(0.6f) else Color(0xFF64748B)
            val itemIconColor  = if (isAppDark) Color.White.copy(0.7f) else Color(0xFF64748B)
            val deleteIconColor = if (isAppDark) Color(0xFFFF8A80) else Color(0xFFEF4444)
            val mainTitleColor = if (isAppDark) Color.White else Color(0xFF475569)

            Dialog(onDismissRequest = { mostrarDialogoHistorico = false }) {
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
                                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = itemTitleColor)
                                    }
                                } else if (viewModel.historico.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("Nenhum histórico encontrado.", color = mainTitleColor.copy(0.7f))
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
                                                    .border(1.dp, if(isAppDark) Color.White.copy(0.1f) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
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
                                                            Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
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
                                                        onClick = { viewModel.deletarUrl(item, {}, {}) },
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
                                    .clickable { mostrarDialogoHistorico = false }
                                    .border(1.dp, if(isAppDark) Color.Transparent else Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Fechar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if(isAppDark) Color.White else Color(0xFF2563EB)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}