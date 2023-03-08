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

        fun isPowerOfTwo(word: Int): Boolean {
            return word and (word - 1) == 0
        }

        fun uniqueBits(potentialUnique: Int, uniqueFrom: Int): Int {
            return (potentialUnique xor uniqueFrom) and potentialUnique
        }

        fun removeBits(removeFrom: Int, bits: Int): Int {
            return removeFrom and bits.inv()
        }
    }
}