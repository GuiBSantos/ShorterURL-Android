package com.example.shortenerapp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel

@Composable
fun EncurtadorScreen(
    viewModel: ShortenerViewModel,
    onNavigateToProfile: () -> Unit
) {
    var urlDigitada by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    var inputCliques by remember { mutableStateOf("") }
    var inputTempo by remember { mutableStateOf("") }
    var unidadeTempo by remember { mutableStateOf("Minutos") }
    val unidades = listOf("Minutos", "Horas", "Dias")
    var expandedUnidade by remember { mutableStateOf(false) }

    var mostrarDialogoHistorico by remember { mutableStateOf(false) }
    var itemParaDeletar by remember { mutableStateOf<HistoricoItem?>(null) }
    var mostrarConfirmacaoDelete by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = {
                viewModel.carregarHistorico()
                mostrarDialogoHistorico = true
            }) {
                Icon(Icons.Default.History, "Histórico", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(Icons.Default.Person, "Perfil", tint = MaterialTheme.colorScheme.secondary)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Shortener", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = urlDigitada,
                onValueChange = { urlDigitada = it },
                label = { Text("Cole sua URL aqui") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = inputCliques,
                    onValueChange = { if (it.all { char -> char.isDigit() }) inputCliques = it },
                    label = { Text("Max. Acessos") },
                    placeholder = { Text("Ex: 10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = inputTempo,
                    onValueChange = { if (it.all { char -> char.isDigit() }) inputTempo = it },
                    label = { Text("Expira em") },
                    placeholder = { Text("Ex: 30") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (inputTempo.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedButton(
                        onClick = { expandedUnidade = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$unidadeTempo")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    DropdownMenu(
                        expanded = expandedUnidade,
                        onDismissRequest = { expandedUnidade = false }
                    ) {
                        unidades.forEach { label ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    unidadeTempo = label
                                    expandedUnidade = false
                                }
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

                    if (expirationMinutes != null && expirationMinutes > 130000) {
                        Toast.makeText(context, "O tempo máximo é de 3 meses.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    viewModel.encurtarUrl(
                        urlDigitada = urlDigitada,
                        maxClicks = maxClicksFinal,
                        expirationTime = expirationMinutes,
                        onSuccess = { shortUrl ->
                            resultado = shortUrl
                            urlDigitada = ""
                            inputCliques = ""
                            inputTempo = ""
                        },
                        onError = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Encurtar Agora")
            }

            if (resultado.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = resultado,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { uriHandler.openUri(resultado) },
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(resultado))
                            Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copiar")
                        }

                        // Botão Compartilhar
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "Olha esse link que eu encurtei: $resultado")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Compartilhar via")
                            context.startActivity(shareIntent)
                        }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                }
            }
        }

        if (mostrarDialogoHistorico) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoHistorico = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Histórico Recente")
                        if (viewModel.isLoadingHistorico.value) {
                            Spacer(modifier = Modifier.width(12.dp))
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                    }
                },
                text = {
                    Box(modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 400.dp), contentAlignment = Alignment.Center) {
                        if (viewModel.isLoadingHistorico.value) {
                            CircularProgressIndicator()
                        } else if (viewModel.historico.isEmpty()) {
                            Text("Nenhum link encontrado.", color = Color.Gray)
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(viewModel.historico) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(4.dp).background(Color.Gray.copy(0.1f), RoundedCornerShape(8.dp)).padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.original, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                                            Text(item.encurtada, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = {
                                            itemParaDeletar = item
                                            mostrarConfirmacaoDelete = true
                                        }) { Icon(Icons.Default.Close, "Excluir", tint = Color.Red) }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { mostrarDialogoHistorico = false }) { Text("Fechar") } }
            )
        }

        if (mostrarConfirmacaoDelete && itemParaDeletar != null) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacaoDelete = false },
                title = { Text("Excluir Link?") },
                text = { Text("Tem certeza que deseja apagar este link?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletarUrl(itemParaDeletar!!,
                            onSuccess = { Toast.makeText(context, "Excluído!", Toast.LENGTH_SHORT).show() },
                            onError = { Toast.makeText(context, "Erro: $it", Toast.LENGTH_SHORT).show() }
                        )
                        mostrarConfirmacaoDelete = false
                    }) { Text("Sim", color = Color.Red) }
                },
                dismissButton = { TextButton(onClick = { mostrarConfirmacaoDelete = false }) { Text("Não") } }
            )
        }
    }
}