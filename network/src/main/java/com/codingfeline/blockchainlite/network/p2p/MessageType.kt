package com.codingfeline.blockchainlite.network.p2p

enum class MessageType {
    QUERY_LATEST,
    QUERY_ALL,
    RESPONSE_BLOCK,
    RESPONSE_BLOCKCHAIN,
    NEW_TRANSACTION
}
