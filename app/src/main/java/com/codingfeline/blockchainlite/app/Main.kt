package com.codingfeline.blockchainlite.app

import com.codingfeline.blockchainlite.core.BlockChain
import com.codingfeline.blockchainlite.core.GenesisBlock
import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.core.account.AccountDatabaseImpl
import com.codingfeline.blockchainlite.core.util.CoreJsonAdapterFactory
import com.codingfeline.blockchainlite.network.api.ApiServer
import com.codingfeline.blockchainlite.network.p2p.Peer
import com.codingfeline.blockchainlite.network.p2p.WebSocketServer
import com.codingfeline.blockchainlite.network.util.NetworkJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")

    val httpPort = args.getOrNull(0)?.toInt() ?: run {
        logger.error("http port is required")
        return
    }
    val webSocketPort = args.getOrNull(1)?.toInt() ?: run {
        logger.error("websocket port is required")
        return
    }
    val peers = args.getOrNull(2)?.let {
        listOf(Peer(it))
    } ?: emptyList()

    val nodeViewHolder = NodeViewHolder(
        accountDatabase = AccountDatabaseImpl(),
        blockChain = BlockChain(blocks = mutableListOf(GenesisBlock))
    )

    val moshi = Moshi.Builder()
        .add(CoreJsonAdapterFactory())
        .add(NetworkJsonAdapterFactory())
        .build()

    val p2pServer = WebSocketServer(nodeViewHolder, moshi)
    val apiServer = ApiServer(nodeViewHolder, moshi)

    p2pServer.apply {
        connectToPeers(peers)
        startServer(webSocketPort)
    }

    apiServer.startServer(httpPort)
}
