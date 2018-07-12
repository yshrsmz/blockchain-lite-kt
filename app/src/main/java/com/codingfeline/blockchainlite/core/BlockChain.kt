package com.codingfeline.blockchainlite.core

import com.codingfeline.blockchainlite.core.block.Block

class BlockChain(
    private val blocks: MutableList<Block> = mutableListOf()
) {

    fun getBlocks(): List<Block> = blocks.toList()

    fun peek(): Block = blocks.last()

    fun isValidNewBlock(newBlock: Block, previousBlock: Block): Boolean {
        return when {
            previousBlock.index + 1 != newBlock.index -> false
            previousBlock.hash != newBlock.previousHash -> false
            Hash.calculate(newBlock) != newBlock.hash -> false
            else -> true
        }
    }

    fun isValidChain(targetBlockChain: List<Block>): Boolean {
        if (targetBlockChain.isEmpty()) {
            return false
        }

        if (targetBlockChain.first() != GenesisBlock) {
            return false
        }

        targetBlockChain.zip(targetBlockChain.drop(1)).forEachIndexed { index, pair ->
            if (isValidNewBlock(pair.second, pair.first)) {
                return false
            }
        }
        return true
    }

    fun append(block: Block) {
        if (isValidNewBlock(block, peek())) {
            blocks += block
        }
    }

    fun replace(newBlocks: List<Block>) {
        if (isValidChain(newBlocks)) {
            synchronized(blocks) {
                blocks.apply {
                    clear()
                    addAll(newBlocks)
                }
            }
        }
    }
}
