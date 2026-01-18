package com.example.shortenerapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.ui.viewmodel.ShortenerViewModel

@Composable
fun EncurtadorScreen(
    viewModel: ShortenerViewModel,
    onLogout: () -> Unit
) {
    var urlDigitada by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
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
            IconButton(onClick = { mostrarDialogoHistorico = true }) {
                Icon(Icons.Default.History, "Histórico", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.Close, "Sair", tint = Color.Red)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Shortener URL ✂️", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = urlDigitada,
                onValueChange = { urlDigitada = it },
                label = { Text("Cole sua URL aqui") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Chama o ViewModel
                    viewModel.encurtarUrl(
                        urlDigitada = urlDigitada,
                        onSuccess = { shortUrl ->
                            resultado = shortUrl
                            urlDigitada = ""
                        },
                        onError = { erro ->
                            Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Encurtar Agora")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (resultado.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(resultado, modifier = Modifier.weight(1f).clickable { uriHandler.openUri(resultado) }, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(resultado))
                            Toast.makeText(context, "Copiado!", Toast.LENGTH_SHORT).show()
                        }) { Icon(Icons.Default.ContentCopy, "Copiar") }
                    }
                }
            }
        }

        if (mostrarDialogoHistorico) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoHistorico = false },
                title = { Text("Histórico Recente") },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {

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