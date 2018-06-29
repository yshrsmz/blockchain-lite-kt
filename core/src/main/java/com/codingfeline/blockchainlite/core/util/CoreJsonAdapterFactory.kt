package com.codingfeline.blockchainlite.core.util

import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.block.BlockJsonAdapter
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.codingfeline.blockchainlite.core.transaction.TransactionJsonAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class CoreJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) return null

        if (type == Block::class.java) {
            return BlockJsonAdapter(moshi)
        }

        if (type == Transaction::class.java) {
            return TransactionJsonAdapter(moshi)
        }

        return null
    }
}
