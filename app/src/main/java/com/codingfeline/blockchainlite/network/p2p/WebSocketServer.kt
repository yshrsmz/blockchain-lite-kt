package com.codingfeline.blockchainlite.network.p2p

import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.FrameType
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.error
import io.ktor.util.flattenForEach
import io.ktor.websocket.webSocket
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.sendBlocking
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.Duration

class WebSocketServer(
        val nodeViewHolder: NodeViewHolder,
        val peerDatabase: PeerDatabase,
        val moshi: Moshi
) {

    private val logger = LoggerFactory.getLogger("WebSocketServer")

    private val webSocketClient = HttpClient(CIO).config { install(WebSockets) }

    private val messageJsonAdapter: JsonAdapter<Message> by lazy { moshi.adapter(Message::class.java) }

    fun startServer(port: Int) {
        embeddedServer(Netty, port) {
            install(DefaultHeaders)
            install(CallLogging)
            install(io.ktor.websocket.WebSockets) {
                pingPeriod = Duration.ofMinutes(1)
            }
            routing {
                webSocket {
                    logger.info("WebSocket Connection request")
                    this.call.request.headers.flattenForEach { s, s2 -> logger.debug("Header: $s - $s2") }
                    val peer = Peer("${this.call.request.origin.scheme}://${this.call.request.host()}:${this.call.request.port()}")
                    initConnection(peer, this)
                }
            }
        }.start(wait = false)
    }

    fun connectToPeers(newPeers: List<Peer>) {
        newPeers.forEach { peer ->
            async<Unit> {
                val uri = URI.create(peer.host)
                logger.debug("connecting to $peer, $uri")

                webSocketClient.ws(method = HttpMethod.Get, host = uri.host, port = uri.port, path = uri.path) {
                    initConnection(peer, this)
                }
            }
        }
    }

    private suspend fun initConnection(peer: Peer, session: WebSocketSession) {
        logger.debug("connection created: $peer")
        peerDatabase.addOrUpdateKnownPeers(peer, session)
        chainLengthMessage(session)

        try {
            session.incoming.consumeEach {
                logger.debug("message received from peer: $it")
                when (it.frameType) {
                    FrameType.TEXT -> handleMessage(session, (it as Frame.Text).readText())
                    else -> {
                        // no-op
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    private fun buildLatestMessage(): String =
            messageJsonAdapter.toJson(Message(type = MessageType.RESPONSE_BLOCK.ordinal, block = nodeViewHolder.blockChain.peek()))

    private fun buildChainMessage(): String =
            messageJsonAdapter.toJson(Message(type = MessageType.RESPONSE_BLOCKCHAIN.ordinal, blockchain = nodeViewHolder.blockChain.getBlocks()))

    private fun buildChainLengthMessage(): String =
            messageJsonAdapter.toJson(Message(type = MessageType.QUERY_LATEST.ordinal))

    private fun buildAllMessage(): String =
            messageJsonAdapter.toJson(Message(type = MessageType.QUERY_ALL.ordinal))

    private fun buildNewTransactionMessage(transactions: List<Transaction>): String = messageJsonAdapter.toJson(Message(type = MessageType.NEW_TRANSACTION.ordinal, transactions = transactions))

    fun chainLengthMessage(session: WebSocketSession) {
        write(session, buildChainLengthMessage())
    }

    fun sendLatestMessage(session: WebSocketSession) {
        write(session, buildLatestMessage())
    }

    fun sendChainMessage(session: WebSocketSession) {
        write(session, buildChainMessage())
    }

    fun broadcastLatestMessage() {
        broadcast(buildLatestMessage())
    }

    fun broadcastAllMessage() {
        broadcast(buildAllMessage())
    }

    fun broadcastNewTransaction(transaction: List<Transaction>) {
        broadcast(buildNewTransactionMessage(transaction))
    }

    private fun write(session: WebSocketSession, message: String) {
        session.outgoing.sendBlocking(Frame.Text(message))
    }

    fun broadcast(message: String) {
        peerDatabase.knownPeers.entries.forEach { write(it.value, message) }
    }

    fun handleBlockchainResponse(receivedBlocks: List<Block>) {
        val latestBlockReceived = receivedBlocks.last()
        val latestBlockHeld = nodeViewHolder.blockChain.peek()
        if (latestBlockReceived.index > latestBlockHeld.index) {
            logger.info("received blockchain is ahead of ours. current head: ${latestBlockHeld.index}, received head: ${latestBlockReceived.index}")
            if (latestBlockHeld.hash == latestBlockReceived.previousHash) {
                logger.info("we have previous hash for the received chain")
                nodeViewHolder.blockChain.append(latestBlockReceived)
                broadcastLatestMessage()
            } else if (receivedBlocks.size == 1) {
                logger.info("we need to query the chain from our peers")
                broadcastAllMessage()
            } else {
                logger.debug("received blockchain is longer than ours")
                nodeViewHolder.blockChain.replace(receivedBlocks)
                broadcastLatestMessage()
            }
        } else {
            logger.info("received blockchain is behind ours. skipping process...")
        }
    }

    fun handleNewTransactionResponse(transactions: List<Transaction>) {
        logger.info("New Transactions received: $transactions")
        transactions.filter { nodeViewHolder.transactionPool.add(it) }
                .let { broadcastNewTransaction(it) }
    }

    private fun handleMessage(from: WebSocketSession, json: String) {
        messageJsonAdapter.fromJson(json)?.let { message ->
            when (message.messageType()) {
                MessageType.QUERY_LATEST -> {
                    sendLatestMessage(from)
                }
                MessageType.QUERY_ALL -> {
                    sendChainMessage(from)
                }
                MessageType.RESPONSE_BLOCK -> {
                    handleBlockchainResponse(listOf(message.block!!))
                }
                MessageType.RESPONSE_BLOCKCHAIN -> {
                    handleBlockchainResponse(message.blockchain!!)
                }
                MessageType.NEW_TRANSACTION -> {
                    handleNewTransactionResponse(message.transactions!!)
                }
            }
        }
    }
}
