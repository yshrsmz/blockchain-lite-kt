package com.codingfeline.blockchainlite.network.api

import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.core.account.Account
import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.codingfeline.blockchainlite.network.p2p.Peer
import com.codingfeline.blockchainlite.network.p2p.PeerDatabase
import com.codingfeline.blockchainlite.network.p2p.WebSocketServer
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

class ApiServer(
        val nodeViewHolder: NodeViewHolder,
        val peerDatabase: PeerDatabase,
        val webSocketServer: WebSocketServer,
        val moshi: Moshi
) {

    private val logger = LoggerFactory.getLogger("ApiServer")

    private val blocksAdapter by lazy { moshi.adapter<List<Block>>(Types.newParameterizedType(MutableList::class.java, Block::class.java)) }

    private val peersAdapter by lazy { moshi.adapter<List<Peer>>(Types.newParameterizedType(MutableList::class.java, Peer::class.java)) }

    private val peerAdapter by lazy { moshi.adapter<Peer>(Peer::class.java) }

    private val transactionsAdapter by lazy { moshi.adapter<List<Transaction>>(Types.newParameterizedType(MutableList::class.java, Transaction::class.java)) }

    private val accountsAdapter by lazy { moshi.adapter<List<Account>>(Types.newParameterizedType(MutableList::class.java, Account::class.java)) }

    fun startServer(port: Int) {
        embeddedServer(Netty, port = port) {
            routing {
                get("/") {
                    call.respondText("Hello, World", ContentType.Text.Plain)
                }

                get("/blocks") {
                    call.respondText(blocksAdapter.toJson(nodeViewHolder.blockChain.getBlocks()), ContentType.Application.Json)
                }

                get("/peers") {
                    call.respondText(peersAdapter.toJson(peerDatabase.knownPeers.keys.toList()), ContentType.Application.Json)
                }

                post("/peers") {
                    val data = call.receiveText()
                    val peer = peerAdapter.fromJson(data)

                    if (peer != null) {
                        webSocketServer.connectToPeers(listOf(peer))
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                get("/transactions") {
                    call.respondText(transactionsAdapter.toJson(nodeViewHolder.transactionPool.transactions), ContentType.Application.Json)
                }

                get("/accounts") {
                    call.respondText(accountsAdapter.toJson(nodeViewHolder.accountDatabase.knownAccounts.values.toList()))
                }

                post("/accounts") {

                }
            }
        }.start(wait = true)
    }
}
