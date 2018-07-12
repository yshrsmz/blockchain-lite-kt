package com.codingfeline.blockchainlite.util

import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.block.BlockJsonAdapter
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.codingfeline.blockchainlite.core.transaction.TransactionJsonAdapter
import com.codingfeline.blockchainlite.network.api.TransactionRequest
import com.codingfeline.blockchainlite.network.api.TransactionRequestJsonAdapter
import com.codingfeline.blockchainlite.network.p2p.Message
import com.codingfeline.blockchainlite.network.p2p.MessageJsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class JsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null

        if (type == Block::class.java) {
            return BlockJsonAdapter(moshi)
        }

        if (type == Transaction::class.java) {
            return TransactionJsonAdapter(moshi)
        }

        if (type == Message::class.java) {
            return MessageJsonAdapter(moshi)
        }

        if (type == TransactionRequest::class.java) {
            return TransactionRequestJsonAdapter(moshi)
        }

        return null

    }
}
