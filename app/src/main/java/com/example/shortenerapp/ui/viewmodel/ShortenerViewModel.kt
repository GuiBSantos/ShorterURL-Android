package com.example.shortenerapp.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shortenerapp.data.model.HistoricoItem
import com.example.shortenerapp.data.model.ShortenUrlRequest
import com.example.shortenerapp.data.repository.UrlRepository
import com.example.shortenerapp.ui.utils.ErrorUtils
import kotlinx.coroutines.launch

class ShortenerViewModel(private val repository: UrlRepository) : ViewModel() {

    val historico = mutableStateListOf<HistoricoItem>()
    var isLoadingHistorico = mutableStateOf(false)
    var isLoadingEncurtar = mutableStateOf(false)

    var userAvatarUrl = mutableStateOf<String?>(null)

    init {
        carregarHistorico()
        carregarPerfilUsuario()
    }

    fun clearState() {
        historico.clear()
        userAvatarUrl.value = null
        isLoadingEncurtar.value = false
        isLoadingHistorico.value = false
    }

    fun carregarPerfilUsuario() {
        viewModelScope.launch {
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    userAvatarUrl.value = if (user.avatarUrl != null) "${user.avatarUrl}?t=${System.currentTimeMillis()}" else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun carregarHistorico() {
        viewModelScope.launch {
            isLoadingHistorico.value = true
            try {
                val result = repository.getUserHistory()

                result.onSuccess { listaBackend ->
                    historico.clear()
                    listaBackend.forEach { item ->
                        val dataFormatada = formatarData(item.expiresAt)

                        historico.add(
                            HistoricoItem(
                                shortCode = item.shortCode,
                                original = item.url,
                                encurtada = item.shortUrl,
                                dataExpiracao = dataFormatada
                            )
                        )
                    }
                }.onFailure { e ->
                    println("Erro ao carregar histórico: ${e.message}")
                }
            } catch (e: Exception) {
                println("Erro crítico: ${ErrorUtils.parseError(e)}")
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
        if (urlLimpa.isEmpty()) { onError("Digite alguma coisa!"); return }

        val urlParaValidar = if (urlLimpa.startsWith("http://") || urlLimpa.startsWith("https://")) urlLimpa else "https://$urlLimpa"

        if (!Patterns.WEB_URL.matcher(urlParaValidar).matches()) { onError("URL inválida"); return }

        isLoadingEncurtar.value = true

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
                    val dataFormatada = formatarData(body.expiresAt)

                    historico.add(0, HistoricoItem(
                        shortCode = body.shortCode,
                        original = body.url,
                        encurtada = body.shortUrl,
                        dataExpiracao = dataFormatada
                    ))
                    onSuccess(body.shortUrl)
                } else {
                    onError("Erro ao encurtar (Cod: ${response.code()})")
                }
            } catch (e: Exception) {
                onError(ErrorUtils.parseError(e))
            } finally {
                isLoadingEncurtar.value = false
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
                onError(ErrorUtils.parseError(e))
            }
        }
    }

    private fun formatarData(dataRaw: Any?): String {
        if (dataRaw == null) return "Sem validade"
        val stringData = dataRaw.toString()
        if (stringData.startsWith("[")) return "Data definida"
        return stringData.replace("T", " ").take(16)
    }
}