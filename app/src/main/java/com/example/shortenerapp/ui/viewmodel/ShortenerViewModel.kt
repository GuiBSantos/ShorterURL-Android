package com.example.shortenerapp.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.repository.UrlRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShortenerViewModel(private val repository: UrlRepository) : ViewModel() {

    val historico = mutableStateListOf<HistoricoItem>()

    var isLoadingHistorico = mutableStateOf(false)

    init {
        carregarHistorico()
    }

    fun carregarHistorico() {
        viewModelScope.launch {

            isLoadingHistorico.value = true

            try {
                val response = repository.getMyUrls()
                if (response.isSuccessful && response.body() != null) {
                    val listaDoBackend = response.body()!!

                    historico.clear()

                    listaDoBackend.forEach { item ->
                        val dataFormatada = item.expiresAt?.replace("T", " ")?.take(16) ?: "Sem data"

                        historico.add(HistoricoItem(
                            shortCode = item.shortCode,
                            original = item.url,
                            encurtada = item.shortUrl,
                            dataExpiracao = dataFormatada
                        ))
                    }
                }
            } catch (e: Exception) {
                println("Erro ao carregar histórico: ${e.message}")
            } finally {
                isLoadingHistorico.value = false
            }
        }
    }

    fun encurtarUrl(
        urlDigitada: String,
        maxClicks: Int? = null,
        expirationTime: Long? = null,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val urlLimpa = urlDigitada.trim()

        if (urlLimpa.isEmpty()) {
            onError("Digite alguma coisa!")
            return
        }

        val urlParaValidar = if (urlLimpa.startsWith("http://") || urlLimpa.startsWith("https://")) {
            urlLimpa
        } else {
            "https://$urlLimpa"
        }

        if (!Patterns.WEB_URL.matcher(urlParaValidar).matches()) {
            onError("URL inválida")
            return
        }

        viewModelScope.launch {
            try {

                val request = ShortenUrlRequest(
                    url = urlParaValidar,
                    maxClicks = maxClicks,
                    expirationTimeInMinutes = expirationTime
                )

                val response = repository.shortenUrl(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    val dataFormatada = body.expiresAt?.replace("T", " ")?.take(16) ?: "Sem data"

                    historico.add(0, HistoricoItem(
                        shortCode = body.shortCode,
                        original = urlParaValidar,
                        encurtada = body.shortUrl,
                        dataExpiracao = dataFormatada
                    )
                    )

                    onSuccess(body.shortUrl)
                } else {
                    onError("Erro ao encurtar: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Falha na conexão: ${e.message}")
            }
        }
    }

    fun deletarUrl(item: HistoricoItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUrl(item.shortCode)
                if (response.isSuccessful) {
                    historico.remove(item)
                    onSuccess()
                } else {
                    onError("Erro ao deletar: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Erro de conexão ao deletar")
            }
        }
    }
}