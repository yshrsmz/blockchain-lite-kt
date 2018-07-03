package com.codingfeline.blockchainlite.core

import com.codingfeline.blockchainlite.core.account.AccountDatabase
import com.codingfeline.blockchainlite.core.transaction.TransactionPool

class NodeViewHolder constructor(
        val accountDatabase: AccountDatabase,
        val transactionPool: TransactionPool,
        val blockChain: BlockChain
)
