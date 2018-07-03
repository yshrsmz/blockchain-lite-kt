package com.codingfeline.blockchainlite.core.transaction

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transaction(
        val from: String,
        val to: String,
        val message: String,
        val quantity: Long,
        val timestamp: Long,
        val hash: String
)
