package com.example.sudoku.util

import org.junit.Assert.*
import org.junit.Test

class BitUtilUnitTest {

    @Test
    fun countBitsTest() {
        assertEquals(0, BitUtil.countBits(0b0))
        assertEquals(1, BitUtil.countBits(0b1))
        assertEquals(1, BitUtil.countBits(0b10))
        assertEquals(2, BitUtil.countBits(0b11))
        assertEquals(3, BitUtil.countBits(0b101010))
        assertEquals(9, BitUtil.countBits(0b111111111))
        assertEquals(32, BitUtil.countBits(-1))
    }

    @Test
    fun oneBitSetTest() {
        assertFalse(BitUtil.oneBitSet(0))
        assertFalse(BitUtil.oneBitSet(3))

        assertTrue(BitUtil.oneBitSet(1))
        assertTrue(BitUtil.oneBitSet(2))
        assertTrue(BitUtil.oneBitSet(16))
        assertTrue(BitUtil.oneBitSet(-2147483648))
    }

    @Test
    fun uniqueBitsTwoWordsTest() {
        assertEquals(0, BitUtil.uniqueBits(0b0110, 0b1110))
        assertEquals(0, BitUtil.uniqueBits(0b1110, 0b1110))
        assertEquals(0b1000, BitUtil.uniqueBits(0b1110, 0b0110))
        assertEquals(0b0110, BitUtil.uniqueBits(0b0110, 0b0))
        assertEquals(0, BitUtil.uniqueBits(0b0, 0b1110))
    }

    @Test
    fun uniqueBitsArrayTest() {
        val arr1 = arrayOf(
            0b000,
            0b110,
            0b110
        )
        val arr2 = arrayOf(
            0b000,
            0b110,
            0b110,
            0b011
        )
        val arr3 = arrayOf(
            0b0000,
            0b0110,
            0b0110,
            0b0011,
            0b1000
        )
        val arr4 = arrayOf(
            0
        )
        val arr5 = arrayOf(
            0b01111,
            0b01001,
            0b10000
        )
        val arr6 = arrayOf(
            0b10000,
            0b11000,
            0b00110,
            0b01101,
            0b11101
        )

        assertEquals(0, BitUtil.uniqueBits(arr1))
        assertEquals(0b1, BitUtil.uniqueBits(arr2))
        assertEquals(0b1001, BitUtil.uniqueBits(arr3))
        assertEquals(0, BitUtil.uniqueBits(arr4))
        assertEquals(0b10110, BitUtil.uniqueBits(arr5))
        assertEquals(0b00010, BitUtil.uniqueBits(arr6))
    }

    @Test
    fun removeBitsTest() {
        assertEquals(0, BitUtil.removeBits(0, 0))
        assertEquals(1, BitUtil.removeBits(1, 0))
        assertEquals(0, BitUtil.removeBits(0, 1))
        assertEquals(1, BitUtil.removeBits(0b101, 0b100))
        assertEquals(0b0110, BitUtil.removeBits(0b1111, 0b1001))
    }

    @Test
    fun listBitsSetTest() {
        assertArrayEquals(emptyArray(), BitUtil.listBitsSet(0b0).toTypedArray())
        assertArrayEquals(arrayOf(0), BitUtil.listBitsSet(0b1).toTypedArray())
        assertArrayEquals(arrayOf(2), BitUtil.listBitsSet(0b100).toTypedArray())
        assertArrayEquals(arrayOf(0,3,4), BitUtil.listBitsSet(0b11001).toTypedArray())
    }
}