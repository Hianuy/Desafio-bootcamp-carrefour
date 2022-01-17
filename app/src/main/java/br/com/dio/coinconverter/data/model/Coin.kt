package br.com.dio.coinconverter.data.model

import java.util.*

enum class Coin(val locale: Locale) {
    USD(locale = Locale.US),
    CAD(locale = Locale.CANADA),

    // pro brasil nao temos uma constante pre definida
    BRL(Locale("pt", "BR")),
    ARS(Locale("es", "AR"))
    ;

    companion object {
        fun getByName(name: String) = values().find { it.name == name } ?: BRL
    }


}