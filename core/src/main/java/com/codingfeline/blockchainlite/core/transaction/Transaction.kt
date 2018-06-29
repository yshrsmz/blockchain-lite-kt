package com.codingfeline.blockchainlite.core.transaction

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Transaction(
    val from: String,
    val to: String,
    val what: String,
    val quantity: Long
)
