package com.codingfeline.blockchainlite.app

import com.codingfeline.blockchainlite.consensus.PoC
import com.codingfeline.blockchainlite.core.BlockChain
import com.codingfeline.blockchainlite.core.GenesisBlock
import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.core.account.AccountDatabaseImpl
import com.codingfeline.blockchainlite.core.transaction.TransactionPool
import com.codingfeline.blockchainlite.network.api.ApiServer
import com.codingfeline.blockchainlite.network.p2p.Peer
import com.codingfeline.blockchainlite.network.p2p.PeerDatabase
import com.codingfeline.blockchainlite.network.p2p.WebSocketServer
import com.codingfeline.blockchainlite.util.JsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

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
            transactionPool = TransactionPool.getDefault(),
            blockChain = BlockChain(blocks = mutableListOf(GenesisBlock))
    )

    val moshi = Moshi.Builder()
            .add(JsonAdapterFactory())
            .build()

    val peerDatabase = PeerDatabase.getDefault()

    val p2pServer = WebSocketServer(nodeViewHolder, peerDatabase, moshi)
    val apiServer = ApiServer(nodeViewHolder, peerDatabase, p2pServer, moshi)

    val consensus = PoC()
    launch(CommonPool) {
        while (true) {
            delay(30, TimeUnit.SECONDS)
            if (consensus.shouldStartRound(System.currentTimeMillis())) {
                logger.debug("should start new consensus round")
            } else {
                logger.debug("not the time")
            }
        }
    }

    p2pServer.apply {
        connectToPeers(peers)
        startServer(webSocketPort)
    }

    apiServer.startServer(httpPort)
}
