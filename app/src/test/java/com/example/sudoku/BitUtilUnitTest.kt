package com.example.sudoku

import org.junit.Assert.*
import org.junit.Test

class BitUtilUnitTest {

    @Test
    fun countBitsTest() {
        assertEquals(BitUtil.countBits(0b0), 0)
        assertEquals(BitUtil.countBits(0b1), 1)
        assertEquals(BitUtil.countBits(0b10), 1)
        assertEquals(BitUtil.countBits(0b11), 2)
        assertEquals(BitUtil.countBits(0b101010), 3)
        assertEquals(BitUtil.countBits(0b111111111), 9)
        assertEquals(BitUtil.countBits(-1), 32)
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
        assertEquals(BitUtil.uniqueBits(0b0110, 0b1110), 0)
        assertEquals(BitUtil.uniqueBits(0b1110, 0b1110), 0)
        assertEquals(BitUtil.uniqueBits(0b1110, 0b0110), 0b1000)
        assertEquals(BitUtil.uniqueBits(0b0110, 0b0), 0b0110)
        assertEquals(BitUtil.uniqueBits(0b0, 0b1110), 0)
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

        assertEquals(BitUtil.uniqueBits(arr1), 0)
        assertEquals(BitUtil.uniqueBits(arr2), 0b1)
        assertEquals(BitUtil.uniqueBits(arr3), 0b1001)
        assertEquals(BitUtil.uniqueBits(arr4), 0)
        assertEquals(BitUtil.uniqueBits(arr5), 0b10110)
    }

    @Test
    fun removeBitsTest() {
        assertEquals(BitUtil.removeBits(0, 0), 0)
        assertEquals(BitUtil.removeBits(1, 0), 1)
        assertEquals(BitUtil.removeBits(0, 1), 0)
        assertEquals(BitUtil.removeBits(0b101, 0b100), 1)
        assertEquals(BitUtil.removeBits(0b1111, 0b1001), 0b0110)
    }

    @Test
    fun listBitsSetTest() {
        assertArrayEquals(BitUtil.listBitsSet(0b0).toTypedArray(), emptyArray())
        assertArrayEquals(BitUtil.listBitsSet(0b1).toTypedArray(), arrayOf(0))
        assertArrayEquals(BitUtil.listBitsSet(0b100).toTypedArray(), arrayOf(2))
        assertArrayEquals(BitUtil.listBitsSet(0b11001).toTypedArray(), arrayOf(0,3,4))
    }
}