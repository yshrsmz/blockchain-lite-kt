package com.codingfeline.blockchainlite.core.account

interface AccountDatabase {
    val isEmpty: Boolean
    val knownAccounts: Map<String, Account>
    val blacklistedAccounts: List<String>

    fun addOrUpdateKnownAccount(account: Account)
    fun blacklistAccount(account: Account, timestamp: Long)
    fun isBlacklisted(account: Account): Boolean
    fun remove(account: Account): Boolean
}

class AccountDatabaseImpl constructor(

) : AccountDatabase {

    private val whitelist = mutableMapOf<String, Account>()
    private val blackList = mutableMapOf<String, Long>()

    override val isEmpty: Boolean
        get() = whitelist.isEmpty()

    override val knownAccounts: Map<String, Account>
        get() = whitelist.toMap()

    override val blacklistedAccounts: List<String>
        get() = blackList.keys.toList()

    override fun addOrUpdateKnownAccount(account: Account) {
        val updated = whitelist[account.name]
            ?.copy(
                nonce = account.nonce,
                balance = account.balance
            )
            ?: account

        whitelist += updated.name to updated
    }

    override fun blacklistAccount(account: Account, timestamp: Long) {
        whitelist.remove(account.name)
        if (!isBlacklisted(account)) {
            blackList += account.name to timestamp
        }
    }

    override fun isBlacklisted(account: Account): Boolean {
        return synchronized(blackList) { blackList.containsKey(account.name) }
    }

    override fun remove(account: Account): Boolean {
        return whitelist.run {
            remove(account.name)
            isNotEmpty()
        }
    }
}
