package br.com.dio.coinconverter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

typealias ExchangeResponse = HashMap<String, ExchangeResponseValue>

@Entity(tableName = "tb_exchange")
data class ExchangeResponseValue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val codein: String,
    val name: String,
    val bid: Double,
)