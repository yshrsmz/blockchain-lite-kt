package com.codingfeline.blockchainlite.core.transaction

interface TransactionPool {
    val transactions: List<Transaction>
    val isEmpty: Boolean

    fun add(transaction: Transaction): Boolean
    fun remove(transaction: Transaction)
    fun clear()

    companion object {
        fun getDefault(): TransactionPool = TransactionPoolImpl()
    }
}


class TransactionPoolImpl constructor(

) : TransactionPool {
    private val transactionSet = mutableSetOf<Transaction>()

    override val transactions: List<Transaction>
        get() = transactionSet.toList()

    override val isEmpty: Boolean
        get() = transactionSet.isEmpty()

    override fun add(transaction: Transaction): Boolean {
        return transactionSet.add(transaction)
    }

    override fun remove(transaction: Transaction) {
        transactionSet.remove(transaction)
    }

    override fun clear() {
        transactionSet.clear()
    }
}
