package com.codingfeline.blockchainlite.network.p2p

import com.codingfeline.blockchainlite.core.block.Block
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val type: Int,
    val block: Block?,
    val blockchain: List<Block>?
) {
    fun messageType() = MessageType.values()[type]
}
