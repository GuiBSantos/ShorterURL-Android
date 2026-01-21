package com.example.shortenerapp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val uriHandler = LocalUriHandler.current

    val isSystemDark = isSystemInDarkTheme()
    val isDark = isSystemDark || MaterialTheme.colorScheme.background.luminance() < 0.5f

    val bgImageRes = if (isDark) R.drawable.bg_dark_main else R.drawable.bg_light_main

    val textColor = Color.White
    val labelColor = textColor.copy(alpha = 0.7f)
    val accentColor = Color(0xFF3B82F6)

    val transparentInputColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        cursorColor = accentColor,
        focusedBorderColor = accentColor.copy(alpha = 0.8f),
        unfocusedBorderColor = textColor.copy(alpha = 0.3f),
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedLabelColor = accentColor,
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
                .background(Color.Black.copy(alpha = if(isDark) 0.3f else 0.1f))
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
                        imageVector = if (isDark) Icons.Outlined.WbSunny else Icons.Default.NightsStay,
                        contentDescription = "Tema",
                        tint = if(isDark) Color.Yellow else textColor
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
                        Icon(Icons.Default.Person, null, tint = textColor)
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
            Text(
                "Simplifique seus links",
                color = labelColor,
                fontSize = 16.sp
            )

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
                                        if (isSelected) accentColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) accentColor else textColor.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { unidadeTempo = unidade }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = unidade,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor.copy(alpha = 0.85f),
                        contentColor = Color.White,
                        disabledContainerColor = labelColor.copy(alpha = 0.2f)
                    ),
                    enabled = urlDigitada.isNotEmpty()
                ) {
                    Text(
                        text = "Encurtar Link",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.7f))

            if (resultado.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))

                        .background(Color.White.copy(0.05f))
                        .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = resultado,
                            modifier = Modifier.weight(1f),
                            color = accentColor,
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
            AlertDialog(
                onDismissRequest = { mostrarDialogoHistorico = false },
                containerColor = if(isDark) Color(0xFF1E293B) else Color.White,
                titleContentColor = if(isDark) Color.White else Color.Black,
                textContentColor = if(isDark) Color.White else Color.Black,
                title = { Text("Histórico Recente") },
                text = {
                    if (viewModel.historico.isEmpty()) {
                        Text("Nenhum histórico encontrado.", color = labelColor)
                    } else {
                        Text("Seus links recentes aparecerão aqui.", color = labelColor)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogoHistorico = false }) {
                        Text("Fechar", color = accentColor)
                    }
                }
            )
        }
    }
}