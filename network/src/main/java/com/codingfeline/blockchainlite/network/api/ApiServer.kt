package com.codingfeline.blockchainlite.network.api

import com.codingfeline.blockchainlite.core.NodeViewHolder
import com.codingfeline.blockchainlite.core.block.Block
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class ApiServer(
    val nodeViewHolder: NodeViewHolder,
    val moshi: Moshi
) {

    private val blocksAdapter by lazy { moshi.adapter<List<Block>>(Types.newParameterizedType(MutableList::class.java, Block::class.java)) }

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

                }

                post("/peers") {

                }

                get("/accounts") {

                }

                post("/accounts") {

                }
            }
        }.start(wait = true)
    }
}
