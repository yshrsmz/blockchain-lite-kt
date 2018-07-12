package com.codingfeline.blockchainlite.consensus

import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.network.p2p.WebSocketServer
import java.util.concurrent.TimeUnit

class PoC constructor(
        private val nodeViewHolder: NodeViewHolder,
        private val p2pServer: WebSocketServer
) : Consensus {

    var previousRoundClosingMillis: Long = -1
        private set

    var isInRound: Boolean = false
        private set

    fun shouldStartRound(currentMillis: Long): Boolean {
        val diff = currentMillis - previousRoundClosingMillis
        return !isInRound && diff >= TimeUnit.MINUTES.toMillis(10)
    }
}

sealed class MessageType

data class MessageA(val a: String) : MessageType()
object MessageB : MessageType()
