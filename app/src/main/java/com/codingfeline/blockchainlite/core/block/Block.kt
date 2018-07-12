package com.codingfeline.blockchainlite.core.block

import com.codingfeline.blockchainlite.core.Hash
import com.codingfeline.blockchainlite.core.transaction.Transaction
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Block(
    val index: Long,
    val timestamp: Long,
    val transactions: List<Transaction>,
    val transactionsCount: Int,
    val previousHash: String,
    val hash: String = Hash.calculate(index, timestamp, transactions, transactionsCount, previousHash)
) {

    companion object {
        fun first(timestamp: Long = System.currentTimeMillis(), previousHash: String = "0"): Block {
            return Block(
                index = 0,
                timestamp = timestamp,
                transactions = emptyList(),
                transactionsCount = 0,
                previousHash = previousHash
            )
        }

        fun next(previous: Block, timestamp: Long = System.currentTimeMillis(), transactions: List<Transaction>): Block {
            return Block(
                index = previous.index + 1,
                timestamp = timestamp,
                transactions = transactions,
                transactionsCount = transactions.size,
                previousHash = previous.hash
            )
        }
    }
}
