package com.example.shortenerapp.ui.utils

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {
    fun parseError(t: Throwable): String {
        return when (t) {

            is ConnectException -> "Não foi possível conectar ao servidor."

            is UnknownHostException -> "Sem conexão com a internet. Verifique seu Wi-Fi ou dados móveis."

            is SocketTimeoutException -> "O servidor demorou para responder. Tente novamente."

            is HttpException -> {
                when (t.code()) {
                    401 -> "Sessão expirada. Faça login novamente."
                    403 -> "Acesso negado."
                    404 -> "Recurso não encontrado."
                    500 -> "Erro interno no servidor."
                    else -> "Erro no servidor (${t.code()})."
                }
            }

            else -> t.message ?: "Ocorreu um erro desconhecido."
        }
    }
}