package com.codingfeline.blockchainlite.network.util

import com.codingfeline.blockchainlite.network.p2p.Message
import com.codingfeline.blockchainlite.network.p2p.MessageJsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class NetworkJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null

        if (type == Message::class.java) {
            return MessageJsonAdapter(moshi)
        }

        return null
    }
}
