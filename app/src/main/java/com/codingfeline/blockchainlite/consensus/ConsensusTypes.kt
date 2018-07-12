package com.codingfeline.blockchainlite.consensus

import com.codingfeline.blockchainlite.core.transaction.Transaction

enum class ConsensusState {
    NO,
    MovedOn,
    Yes
}

class ConsensusResult {
    val transactions: MutableList<Transaction> = mutableListOf()

    var state: ConsensusState = ConsensusState.NO
}
