package com.codingfeline.blockchainlite.network.p2p

import io.ktor.http.cio.websocket.WebSocketSession

interface PeerDatabase {
    val knownPeers: Map<Peer, WebSocketSession>
    val blacklistedPeers: List<Peer>

    fun addOrUpdateKnownPeers(peer: Peer, session: WebSocketSession)
    fun remove(peer: Peer)

    fun blacklistPeer(peer: Peer)
    fun isBlacklisted(peer: Peer): Boolean

    companion object {
        fun getDefault(): PeerDatabase = PeerDatabaseImpl()
    }
}


class PeerDatabaseImpl constructor(

) : PeerDatabase {
    private val whitelist = hashMapOf<Peer, WebSocketSession>()
    private val blacklist = hashMapOf<Peer, Long>()


    override val knownPeers: Map<Peer, WebSocketSession>
        get() = whitelist.toMap()

    override val blacklistedPeers: List<Peer>
        get() = blacklist.keys.toList()

    override fun addOrUpdateKnownPeers(peer: Peer, session: WebSocketSession) {
        if (isBlacklisted(peer)) return

        whitelist += peer to session
    }

    override fun remove(peer: Peer) {
        whitelist.remove(peer)
    }

    override fun blacklistPeer(peer: Peer) {
        whitelist.remove(peer)
        blacklist += peer to System.currentTimeMillis()
    }

    override fun isBlacklisted(peer: Peer): Boolean {
        return blacklist.containsKey(peer)
    }
}
