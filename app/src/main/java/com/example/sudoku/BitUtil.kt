package com.example.sudoku

class BitUtil {
    companion object {
        fun countBits(word: Int): Int {
            var count = 0
            var wordCopy = word
            while (wordCopy != 0) {
                wordCopy = wordCopy and (wordCopy - 1)
                count++
            }
            return count
        }

        fun oneBitSet(word: Int): Boolean {
            return word != 0 && word and (word - 1) == 0
        }

        fun uniqueBits(potentialUnique: Int, uniqueFrom: Int): Int {
            return (potentialUnique xor uniqueFrom) and potentialUnique
        }

        fun uniqueBits(arr: Array<Int>): Int {
            var duplicateCandidates = 0
            return arr.reduce { acc, num ->
                duplicateCandidates = duplicateCandidates or (acc and num)
                (acc xor num) and duplicateCandidates.inv()
            }
        }

        fun removeBits(removeFrom: Int, bits: Int): Int {
            return removeFrom and bits.inv()
        }

        fun listBitsSet(word: Int): MutableList<Int> {
            val bits: MutableList<Int> = mutableListOf()
            var wordCopy = word
            var pos = 0

            while (wordCopy != 0) {
                if (wordCopy and 1 == 1)
                    bits.add(pos)
                pos++
                wordCopy = wordCopy ushr 1
            }

            return bits
        }
    }
}