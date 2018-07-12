package com.codingfeline.blockchainlite.network.api

import com.codingfeline.blockchainlite.core.Hash
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionRequest(
        val from: String,
        val to: String,
        val message: String = "",
        val quantity: Long,
        val timestamp: Long
)

fun TransactionRequest.toEntity() = Transaction(
        from = from,
        to = to,
        message = message,
        quantity = quantity,
        timestamp = timestamp,
        hash = Hash.calculate("$from$to$message$quantity$timestamp".toByteArray(Charsets.UTF_8))
)
