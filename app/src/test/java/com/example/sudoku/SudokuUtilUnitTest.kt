package com.example.sudoku

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SudokuUtilUnitTest {

    private val fullSudoku = arrayOf(
        arrayOf(9, 7, 2, 6, 1, 4, 3, 8, 5),
        arrayOf(6, 5, 8, 3, 7, 9, 2, 1, 4),
        arrayOf(1, 3, 4, 5, 2, 8, 7, 9, 6),
        arrayOf(4, 8, 7, 9, 6, 3, 1, 5, 2),
        arrayOf(2, 6, 3, 4, 5, 1, 9, 7, 8),
        arrayOf(5, 9, 1, 2, 8, 7, 4, 6, 3),
        arrayOf(3, 4, 5, 1, 9, 6, 8, 2, 7),
        arrayOf(7, 1, 6, 8, 4, 2, 5, 3, 9),
        arrayOf(8, 2, 9, 7, 3, 5, 6, 4, 1)
    )

    private fun Array<Array<Int>>.copy() = Array(size) { get(it).clone() }

    @Test
    fun getRelevantValuesTest() {
        assertArrayEquals(SudokuUtil.getRelevantValues(fullSudoku, 4, 4),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7, 2, 6, 3, 9, 7, 8, 1, 7, 2, 9, 4, 3))

        assertArrayEquals(SudokuUtil.getRelevantValues(fullSudoku, 0, 0),
            arrayOf(9, 7, 2, 6, 5, 8, 1, 3, 4, 6, 1, 4, 3, 8, 5, 4, 2, 5, 3, 7, 8))

        assertArrayEquals(SudokuUtil.getRelevantValues(fullSudoku, 8, 8),
            arrayOf(8, 2, 7, 5, 3 ,9, 6, 4, 1, 8, 2, 9, 7, 3, 5, 5, 4, 6, 2, 8, 3))
    }

    @Test
    fun getRelevantCoordsTest() {
        assertArrayEquals(arrayOf(
            Pair(3, 3),
            Pair(3, 4),
            Pair(3, 5),
            Pair(4, 3),
            Pair(4, 4),
            Pair(4, 5),
            Pair(5, 3),
            Pair(5, 4),
            Pair(5, 5),
            Pair(4, 0),
            Pair(4, 1),
            Pair(4, 2),
            Pair(4, 6),
            Pair(4, 7),
            Pair(4, 8),
            Pair(0, 5),
            Pair(1, 5),
            Pair(2, 5),
            Pair(6, 5),
            Pair(7, 5),
            Pair(8, 5)
        ), SudokuUtil.getRelevantCoords(4, 5))
    }

    @Test
    fun getRowTest() {
        assertArrayEquals(SudokuUtil.getRow(fullSudoku, 0),
            arrayOf(9, 7, 2, 6, 1, 4, 3, 8, 5))
    }

    @Test
    fun getColumnTest() {
        assertArrayEquals(SudokuUtil.getColumn(fullSudoku, 0),
            arrayOf(9, 6, 1, 4, 2, 5, 3, 7, 8))
    }

    @Test
    fun getSquareRowColTest() {
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 4, 4),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 0, 0),
            arrayOf(9, 7, 2, 6, 5, 8, 1, 3, 4))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 5, 4),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 4, 5),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 3, 4),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 4, 3),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 3, 3),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
    }

    @Test
    fun getSquareIndexTest() {
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 0),
            arrayOf(9, 7, 2, 6, 5, 8, 1, 3, 4))
        assertArrayEquals(SudokuUtil.getSquare(fullSudoku, 4),
            arrayOf(9, 6, 3, 4, 5, 1, 2, 8, 7))
    }

    @Test
    fun coordsSquareConversionTest() {
        assertEquals(Pair(0, 0), SudokuUtil.coordsSquareConversion(0, 0))
        assertEquals(Pair(3, 3), SudokuUtil.coordsSquareConversion(4, 0))
        assertEquals(Pair(4, 5), SudokuUtil.coordsSquareConversion(4, 5))
    }

    @Test
    fun getSquareAsMatTest() {
        assertArrayEquals(SudokuUtil.getSquareAsMat(fullSudoku, 4, 3),
            arrayOf(
                arrayOf(9, 6, 3),
                arrayOf(4, 5, 1),
                arrayOf(2, 8, 7)
            ))
    }

    @Test
    fun applyToRelevantValuesTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToRelevantValues(sudokuCopy, 4, 3) {
            it + 1
        }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(9, 7, 2, 7, 1, 4, 3, 8, 5),
            arrayOf(6, 5, 8, 4, 7, 9, 2, 1, 4),
            arrayOf(1, 3, 4, 6, 2, 8, 7, 9, 6),
            arrayOf(4, 8, 7, 10, 7, 4, 1, 5, 2),
            arrayOf(3, 7, 4, 5, 6, 2, 10, 8, 9),
            arrayOf(5, 9, 1, 3, 9, 8, 4, 6, 3),
            arrayOf(3, 4, 5, 2, 9, 6, 8, 2, 7),
            arrayOf(7, 1, 6, 9, 4, 2, 5, 3, 9),
            arrayOf(8, 2, 9, 8, 3, 5, 6, 4, 1)
        ))
    }

    @Test
    fun applyToRowTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToRow(sudokuCopy, 4) {
            0
        }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(9, 7, 2, 6, 1, 4, 3, 8, 5),
            arrayOf(6, 5, 8, 3, 7, 9, 2, 1, 4),
            arrayOf(1, 3, 4, 5, 2, 8, 7, 9, 6),
            arrayOf(4, 8, 7, 9, 6, 3, 1, 5, 2),
            arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
            arrayOf(5, 9, 1, 2, 8, 7, 4, 6, 3),
            arrayOf(3, 4, 5, 1, 9, 6, 8, 2, 7),
            arrayOf(7, 1, 6, 8, 4, 2, 5, 3, 9),
            arrayOf(8, 2, 9, 7, 3, 5, 6, 4, 1)
        ))
    }

    @Test
    fun applyToColumnTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToColumn(sudokuCopy, 4) {
            0
        }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(9, 7, 2, 6, 0, 4, 3, 8, 5),
            arrayOf(6, 5, 8, 3, 0, 9, 2, 1, 4),
            arrayOf(1, 3, 4, 5, 0, 8, 7, 9, 6),
            arrayOf(4, 8, 7, 9, 0, 3, 1, 5, 2),
            arrayOf(2, 6, 3, 4, 0, 1, 9, 7, 8),
            arrayOf(5, 9, 1, 2, 0, 7, 4, 6, 3),
            arrayOf(3, 4, 5, 1, 0, 6, 8, 2, 7),
            arrayOf(7, 1, 6, 8, 0, 2, 5, 3, 9),
            arrayOf(8, 2, 9, 7, 0, 5, 6, 4, 1)
        ))
    }

    @Test
    fun applyToSquareRowColTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToSquare(sudokuCopy, 4, 3) {
            0
        }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(9, 7, 2, 6, 1, 4, 3, 8, 5),
            arrayOf(6, 5, 8, 3, 7, 9, 2, 1, 4),
            arrayOf(1, 3, 4, 5, 2, 8, 7, 9, 6),
            arrayOf(4, 8, 7, 0, 0, 0, 1, 5, 2),
            arrayOf(2, 6, 3, 0, 0, 0, 9, 7, 8),
            arrayOf(5, 9, 1, 0, 0, 0, 4, 6, 3),
            arrayOf(3, 4, 5, 1, 9, 6, 8, 2, 7),
            arrayOf(7, 1, 6, 8, 4, 2, 5, 3, 9),
            arrayOf(8, 2, 9, 7, 3, 5, 6, 4, 1)
        ))
    }

    @Test
    fun applyToSquareIndexTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToSquare(sudokuCopy, 4) {
            0
        }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(9, 7, 2, 6, 1, 4, 3, 8, 5),
            arrayOf(6, 5, 8, 3, 7, 9, 2, 1, 4),
            arrayOf(1, 3, 4, 5, 2, 8, 7, 9, 6),
            arrayOf(4, 8, 7, 0, 0, 0, 1, 5, 2),
            arrayOf(2, 6, 3, 0, 0, 0, 9, 7, 8),
            arrayOf(5, 9, 1, 0, 0, 0, 4, 6, 3),
            arrayOf(3, 4, 5, 1, 9, 6, 8, 2, 7),
            arrayOf(7, 1, 6, 8, 4, 2, 5, 3, 9),
            arrayOf(8, 2, 9, 7, 3, 5, 6, 4, 1)
        ))
    }

    @Test
    fun applyToGridTest() {
        val sudokuCopy = fullSudoku.copy()
        SudokuUtil.applyToGrid(sudokuCopy) { -1 * it }
        assertArrayEquals(sudokuCopy, arrayOf(
            arrayOf(-9, -7, -2, -6, -1, -4, -3, -8, -5),
            arrayOf(-6, -5, -8, -3, -7, -9, -2, -1, -4),
            arrayOf(-1, -3, -4, -5, -2, -8, -7, -9, -6),
            arrayOf(-4, -8, -7, -9, -6, -3, -1, -5, -2),
            arrayOf(-2, -6, -3, -4, -5, -1, -9, -7, -8),
            arrayOf(-5, -9, -1, -2, -8, -7, -4, -6, -3),
            arrayOf(-3, -4, -5, -1, -9, -6, -8, -2, -7),
            arrayOf(-7, -1, -6, -8, -4, -2, -5, -3, -9),
            arrayOf(-8, -2, -9, -7, -3, -5, -6, -4, -1)
        ))
    }
}