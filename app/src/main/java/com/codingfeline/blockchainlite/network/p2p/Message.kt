package com.codingfeline.blockchainlite.network.p2p

import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
        val type: Int,
        val block: Block? = null,
        val blockchain: List<Block>? = null,
        val transactions: List<Transaction>? = null
) {
    fun messageType() = MessageType.values()[type]
}
