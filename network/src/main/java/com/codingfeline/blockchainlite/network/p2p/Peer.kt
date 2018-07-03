package com.codingfeline.blockchainlite.network.p2p

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Peer(val host: String)
