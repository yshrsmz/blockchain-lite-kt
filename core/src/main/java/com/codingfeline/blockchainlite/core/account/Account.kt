package com.codingfeline.blockchainlite.core.account

data class Account(
    val name: String,
    val nonce: Long = 0,
    val balance: Long = 0
) {
}
