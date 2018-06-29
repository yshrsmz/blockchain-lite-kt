package com.codingfeline.blockchainlite.core

import com.codingfeline.blockchainlite.core.block.Block
import com.codingfeline.blockchainlite.core.transaction.Transaction
import java.security.MessageDigest

object Hash {

    fun calculate(
        index: Long,
        timestamp: Long,
        transactions: List<Transaction>,
        transactionsCount: Int,
        previousHash: String
    ): String {
        val md = MessageDigest.getInstance("SHA-256")
        val targetData = "$index$timestamp$transactions$transactionsCount$previousHash".toByteArray(Charsets.UTF_8)
        val digest = md.digest(targetData)
        return digest.fold("") { str, byte -> str + "%02x".format(byte) }
    }

    fun calculate(block: Block): String = calculate(block.index, block.timestamp, block.transactions, block.transactionsCount, block.previousHash)
}
