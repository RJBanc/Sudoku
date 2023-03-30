package com.example.sudoku

import org.junit.Assert.*
import org.junit.Test

class SudokuSolverUnitTest {

    @Test
    fun constructorTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   null,   "4",    "3",    "8",    "5"),
            arrayOf(null,    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        )

        val solver = SudokuSolver(sudoku)
        assertArrayEquals(solver.candidates, arrayOf(
            arrayOf(0b000000000, 0b001100000, 0b000000000, 0b000100000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000100000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000001000, 0b000000000, 0b000000010, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000100000, 0b000000000, 0b000000000, 0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000010000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000001000, 0b000011000, 0b000000000, 0b100000000, 0b000000000, 0b010000000, 0b000000000, 0b001000000),
            arrayOf(0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000100, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000001000, 0b000000001)
        ))
    }

    @Test
    fun addNumberStringTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   null,   "4",    "3",    "8",    "5"),
            arrayOf(null,    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        )

        val solver = SudokuSolver(sudoku)
        solver.addNumber("6", 1, 0)
        assertArrayEquals(solver.candidates, arrayOf(
            arrayOf(0b000000000, 0b001000000, 0b000000000, 0b000100000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000001000, 0b000000000, 0b000000010, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000100000, 0b000000000, 0b000000000, 0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000010000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000001000, 0b000011000, 0b000000000, 0b100000000, 0b000000000, 0b010000000, 0b000000000, 0b001000000),
            arrayOf(0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000100, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000001000, 0b000000001)
        ))
        assertArrayEquals(sudoku, arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   null,   "4",    "3",    "8",    "5"),
            arrayOf("6",    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        ))
    }

    @Test
    fun addNumberNumTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   null,   "4",    "3",    "8",    "5"),
            arrayOf(null,    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        )

        val solver = SudokuSolver(sudoku)
        solver.addNumber(0b000100000, 1, 0)
        assertArrayEquals(solver.candidates, arrayOf(
            arrayOf(0b000000000, 0b001000000, 0b000000000, 0b000100000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000001000, 0b000000000, 0b000000010, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000100000, 0b000000000, 0b000000000, 0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000010000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000001000, 0b000011000, 0b000000000, 0b100000000, 0b000000000, 0b010000000, 0b000000000, 0b001000000),
            arrayOf(0b000000000, 0b000000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000100, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000001000, 0b000000001)
        ))
        assertArrayEquals(sudoku, arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   null,   "4",    "3",    "8",    "5"),
            arrayOf("6",    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        ))
    }

    @Test
    fun singleCandidatePositionTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf(null,    null,    null,    null,    null,    "4",    null,    "2",    "8"),
            arrayOf("4",    null,    "6",    null,    null,    null,    null,    null,    "5"),
            arrayOf("1",    null,    null,    null,    "3",    null,    "6",    null,    null),
            arrayOf(null,    null,    null,    "3",    null,    "1",    null,    null,    null),
            arrayOf(null,    "8",    "7",    null,    null,    null,    "1",    "4",    null),
            arrayOf(null,    null,    null,    "7",    null,    "9",    null,    null,    null),
            arrayOf(null,    null,    "2",    null,    "1",    null,    null,    null,    "3"),
            arrayOf("9",    null,    null,    null,    null,    null,    "5",    null,    "7"),
            arrayOf("6",    "7",    null,    "4",    null,    null,    null,    null,    null)
        )
         val solver = SudokuSolver(sudoku)

        assertEquals(1100, solver.singleCandidatePosition())
        assertArrayEquals(arrayOf<Array<String?>>(
            arrayOf("7",    null,    null,    "1",    null,    "4",    null,    "2",    "8"),
            arrayOf("4",    null,    "6",    null,    null,    null,    null,    "1",    "5"),
            arrayOf("1",    null,    "8",    null,    "3",    null,    "6",    null,    "4"),
            arrayOf(null,    null,    null,    "3",    null,    "1",    null,    null,    null),
            arrayOf("3",    "8",    "7",    null,    null,    null,    "1",    "4",    "9"),
            arrayOf(null,    null,    null,    "7",    null,    "9",    null,    null,    null),
            arrayOf("8",    null,    "2",    null,    "1",    "7",    "4",    null,    "3"),
            arrayOf("9",    null,    null,    null,    null,    null,    "5",    null,    "7"),
            arrayOf("6",    "7",    null,    "4",    null,    null,    null,    null,    "1")
        ), sudoku)

        assertEquals(700, solver.singleCandidatePosition())
        assertArrayEquals(arrayOf<Array<String?>>(
            arrayOf("7",    null,    null,    "1",    "6",    "4",    null,    "2",    "8"),
            arrayOf("4",    null,    "6",    null,    "7",    null,    null,    "1",    "5"),
            arrayOf("1",    null,    "8",    null,    "3",    null,    "6",    "7",    "4"),
            arrayOf(null,    null,    null,    "3",    null,    "1",    null,    null,    null),
            arrayOf("3",    "8",    "7",    null,    null,    null,    "1",    "4",    "9"),
            arrayOf(null,    null,    null,    "7",    null,    "9",    null,    "3",    null),
            arrayOf("8",    "5",    "2",    null,    "1",    "7",    "4",    null,    "3"),
            arrayOf("9",    null,    null,    null,    null,    null,    "5",    null,    "7"),
            arrayOf("6",    "7",    "3",    "4",    null,    null,    "2",    null,    "1")
        ), sudoku)


        val sudokuNoEasy = arrayOf<Array<String?>>(
            arrayOf(null,    "5",    null,    null,    "3",    null,    "6",    null,    "2"),
            arrayOf("6",    "4",    "2",    "8",    "9",    "5",    "3",    "1",    "7"),
            arrayOf(null,    "3",    "7",    null,    "2",    null,    "8",    null,    null),
            arrayOf(null,    "2",    "3",    "5",    null,    "4",    "7",    null,    null),
            arrayOf("4",    null,    "6",    null,    null,    null,    "5",    "2",    null),
            arrayOf("5",    "7",    "1",    "9",    "6",    "2",    "4",    "8",    "3"),
            arrayOf("2",    "1",    "4",    null,    null,    null,    "9",    null,    null),
            arrayOf("7",    "6",    null,    "1",    null,    "9",    "2",    "3",    "4"),
            arrayOf("3",    null,    null,    "2",    "4",    null,    "1",    "7",    null)
        )
        val solverNoEasy = SudokuSolver(sudokuNoEasy)
        assertEquals(0, solverNoEasy.singleCandidatePosition())
        assertArrayEquals(arrayOf<Array<String?>>(
            arrayOf(null,    "5",    null,    null,    "3",    null,    "6",    null,    "2"),
            arrayOf("6",    "4",    "2",    "8",    "9",    "5",    "3",    "1",    "7"),
            arrayOf(null,    "3",    "7",    null,    "2",    null,    "8",    null,    null),
            arrayOf(null,    "2",    "3",    "5",    null,    "4",    "7",    null,    null),
            arrayOf("4",    null,    "6",    null,    null,    null,    "5",    "2",    null),
            arrayOf("5",    "7",    "1",    "9",    "6",    "2",    "4",    "8",    "3"),
            arrayOf("2",    "1",    "4",    null,    null,    null,    "9",    null,    null),
            arrayOf("7",    "6",    null,    "1",    null,    "9",    "2",    "3",    "4"),
            arrayOf("3",    null,    null,    "2",    "4",    null,    "1",    "7",    null)
        ), sudokuNoEasy)
    }

    @Test
    fun singleCandidatePositionExceptionsTest() {
        val sudokuNoCand = arrayOf<Array<String?>>(
            arrayOf("9",    null,   "2",    null,   "6",   "4",    "3",    "8",    "5"),
            arrayOf(null,    "5",   "8",    "3",    "7",    "9",    "2",    "1",    "4"),
            arrayOf("1",    "3",    null,   "5",    null,   "8",    "7",    "9",    "6"),
            arrayOf("4",    "8",    "7",    "9",    "6",    "3",    "1",    "5",    "2"),
            arrayOf("2",    null,   "3",    "4",    "5",    null,   "9",    "7",    "8"),
            arrayOf(null,   "9",    "1",    "2",    "8",    "7",    "4",    "6",    "3"),
            arrayOf("3",    null,   null,   "1",    null,   "6",    null,   "2",    null),
            arrayOf("7",    null,   "6",    "8",    "4",    "2",    "5",    null,   "9"),
            arrayOf("8",    "2",    "9",    "7",    "3",    "5",    "6",    null,   null)
        )

        val solverNoCand = SudokuSolver(sudokuNoCand)
        val exc1 = assertThrows(NoSolutionException::class.java) {
            solverNoCand.singleCandidatePosition()
        }
        assertEquals("Empty field has no possible candidates", exc1.message)

        val sudokuTwoUnique = arrayOf<Array<String?>>(
            arrayOf("7",    null,    null,    "1",    null,    "4",    null,    "2",    "8"),
            arrayOf("4",    null,    "6",    null,    null,    null,    "9",    "1",    "5"),
            arrayOf("1",    null,    "8",    null,    "3",    null,    "6",    null,    "4"),
            arrayOf(null,    null,    null,    "3",    null,    "1",    null,    null,    null),
            arrayOf("3",    "8",    "7",    "9",    null,    null,    "1",    "4",    "9"),
            arrayOf(null,    null,    null,    "7",    null,    "9",    null,    null,    null),
            arrayOf("8",    null,    "2",    null,    "1",    "7",    "4",    null,    "3"),
            arrayOf("9",    null,    null,    null,    null,    null,    "5",    null,    "7"),
            arrayOf("6",    "7",    null,    "4",    null,    null,    null,    null,    "1")
        )

        val solverTwoUnique = SudokuSolver(sudokuTwoUnique)
        val exc2 = assertThrows(NoSolutionException::class.java) {
            solverTwoUnique.singleCandidatePosition()
        }
        assertEquals("Single Position for multiple numbers", exc2.message)
    }

    @Test
    fun candidateLinesTest() {
        val sudokuHor = arrayOf<Array<String?>>(
            arrayOf(null,    "1",    "7",    "9",    null,    "3",    "6",    null,    null),
            arrayOf(null,    null,    null,    null,    "8",    null,    null,    null,    null),
            arrayOf("9",    null,    null,    null,    null,    null,    "5",    null,    "7"),
            arrayOf(null,    "7",    "2",    null,    "1",    null,    "4",    "3",    null),
            arrayOf(null,    null,    null,    "4",    null,    "2",    null,    "7",    null),
            arrayOf(null,    "6",    "4",    "3",    "7",    null,    "2",    "5",    null),
            arrayOf("7",    null,    "1",    null,    null,    null,    null,    "6",    "5"),
            arrayOf(null,    null,    null,    null,    "3",    null,    null,    null,    null),
            arrayOf(null,    null,    "5",    "6",    null,    "1",    "7",    "2",    null)
        )
        val solverHor = SudokuSolver(sudokuHor)
        assertEquals(200, solverHor.candidateLines())
        assertArrayEquals(arrayOf(
            arrayOf(0b010011010, 0b000000000, 0b000000000, 0b000000000, 0b000011010, 0b000000000, 0b000000000, 0b010001000, 0b010001010),
            arrayOf(0b000111010, 0b000011010, 0b000100000, 0b001010011, 0b000000000, 0b001111000, 0b100000101, 0b100001001, 0b100001111),
            arrayOf(0b000000000, 0b010001110, 0b010100100, 0b000000011, 0b000101010, 0b000101000, 0b000000000, 0b010001001, 0b000000000),
            arrayOf(0b010010000, 0b000000000, 0b000000000, 0b010010000, 0b000000000, 0b110110000, 0b000000000, 0b000000000, 0b110100000),
            arrayOf(0b010010101, 0b110010100, 0b110000100, 0b000000000, 0b100110000, 0b000000000, 0b110000001, 0b000000000, 0b110100001),
            arrayOf(0b010000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b110000000, 0b000000000, 0b000000000, 0b110000001),
            arrayOf(0b000000000, 0b110001110, 0b000000000, 0b010000010, 0b100001010, 0b110001000, 0b110000100, 0b000000000, 0b000000000),
            arrayOf(0b010101010, 0b110001010, 0b110100000, 0b011010010, 0b000000000, 0b111011000, 0b110000001, 0b110001001, 0b110001001),
            arrayOf(0b010001100, 0b110001100, 0b000000000, 0b000000000, 0b100001000, 0b000000000, 0b000000000, 0b000000000, 0b110001100)
        ), solverHor.candidates)

        val sudokuVert = arrayOf<Array<String?>>(
            arrayOf(null,    null,    null,    "7",    "2",    "4",    null,    null,    "5"),
            arrayOf(null,    "2",    null,    null,    "1",    null,    null,    "7",    null),
            arrayOf(null,    null,    null,    null,    "8",    null,    null,    null,    "2"),
            arrayOf(null,    "9",    null,    null,    "3",    "6",    "2",    "5",    null),
            arrayOf("6",    null,    "2",    null,    "7",    null,    null,    null,    "8"),
            arrayOf(null,    "5",    "3",    "2",    "4",    null,    null,    "1",    null),
            arrayOf("4",    null,    null,    "3",    "9",    null,    null,    "2",    null),
            arrayOf(null,    "3",    null,    null,    "6",    "2",    null,    "9",    null),
            arrayOf("2",    null,    "9",    "4",    "5",    "7",    null,    null,    null)
        )
        val solverVert = SudokuSolver(sudokuVert)
        assertEquals(200, solverVert.candidateLines())
        assertArrayEquals(arrayOf(
            arrayOf(0b110000101, 0b010100001, 0b010100001, 0b000000000, 0b000000000, 0b000000000, 0b110100101, 0b010100100, 0b000000000),
            arrayOf(0b110010100, 0b000000000, 0b010111000, 0b100110000, 0b000000000, 0b100010100, 0b110101100, 0b000000000, 0b100101100),
            arrayOf(0b101010101, 0b001101001, 0b001111001, 0b100110000, 0b000000000, 0b100010100, 0b100101101, 0b000101100, 0b000000000),
            arrayOf(0b011000001, 0b000000000, 0b011001001, 0b010000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b001001000),
            arrayOf(0b000000000, 0b000001001, 0b000000000, 0b100010001, 0b000000000, 0b100010001, 0b100001100, 0b000001100, 0b000000000),
            arrayOf(0b011000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b110000000, 0b101100000, 0b000000000, 0b101100000),
            arrayOf(0b000000000, 0b011100001, 0b011110001, 0b000000000, 0b000000000, 0b010000001, 0b011110000, 0b000000000, 0b001100001),
            arrayOf(0b011010001, 0b000000000, 0b011010001, 0b010000001, 0b000000000, 0b000000000, 0b011011000, 0b000000000, 0b001001001),
            arrayOf(0b000000000, 0b010100001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b010100100, 0b010100100, 0b000100101)
        ), solverVert.candidates)
    }

    @Test
    fun boxLineReductionTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf(null,    "1",    "6",    null,    null,    "7",    "8",    null,    "3"),
            arrayOf(null,    "9",    null,    "8",    null,    null,    null,    null,    null),
            arrayOf("8",    "7",    null,    null,    null,    "1",    null,    "6",    null),
            arrayOf(null,    "4",    "8",    null,    null,    null,    "3",    null,    null),
            arrayOf("6",    "5",    null,    null,    null,    "9",    null,    "8",    "2"),
            arrayOf(null,    "3",    "9",    null,    null,    null,    "6",    "5",    null),
            arrayOf(null,    "6",    null,    "9",    null,    null,    null,    "2",    null),
            arrayOf(null,    "8",    null,    null,    null,    "2",    "9",    "3",    "6"),
            arrayOf("9",    "2",    "4",    "6",    null,    null,    "5",    "1",    null)
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(350, solver.boxLineReduction())
        assertArrayEquals(arrayOf(
            arrayOf(0b000011000, 0b000000000, 0b000000000, 0b000011010, 0b100011010, 0b000000000, 0b000000000, 0b100001000, 0b000000000),
            arrayOf(0b000011100, 0b000000000, 0b000010110, 0b000000000, 0b000111110, 0b000111100, 0b001001011, 0b001001000, 0b001011001),
            arrayOf(0b000000000, 0b000000000, 0b000010110, 0b000011110, 0b100011110, 0b000000000, 0b000001010, 0b000000000, 0b100011000),
            arrayOf(0b001000011, 0b000000000, 0b000000000, 0b001010011, 0b001110011, 0b000110000, 0b000000000, 0b101000000, 0b101000001),
            arrayOf(0b000000000, 0b000000000, 0b001000001, 0b001001101, 0b001001101, 0b000000000, 0b001001001, 0b000000000, 0b000000000),
            arrayOf(0b001000011, 0b000000000, 0b000000000, 0b001001011, 0b011001011, 0b010001000, 0b000000000, 0b000000000, 0b001001001),
            arrayOf(0b001010101, 0b000000000, 0b001010101, 0b000000000, 0b011011101, 0b010011100, 0b001001000, 0b000000000, 0b011001000),
            arrayOf(0b001010001, 0b000000000, 0b001010001, 0b001011001, 0b001011001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b011000100, 0b010000100, 0b000000000, 0b000000000, 0b011000000)
        ), solver.candidates)

        assertEquals(350, solver.boxLineReduction())
        assertArrayEquals(arrayOf(
            arrayOf(0b000011000, 0b000000000, 0b000000000, 0b000011010, 0b100011010, 0b000000000, 0b000000000, 0b100001000, 0b000000000),
            arrayOf(0b000011100, 0b000000000, 0b000010110, 0b000000000, 0b000111100, 0b000111100, 0b001001011, 0b001001000, 0b001011001),
            arrayOf(0b000000000, 0b000000000, 0b000010110, 0b000011100, 0b100011100, 0b000000000, 0b000001010, 0b000000000, 0b100011000),
            arrayOf(0b001000011, 0b000000000, 0b000000000, 0b001010011, 0b001110011, 0b000110000, 0b000000000, 0b101000000, 0b101000001),
            arrayOf(0b000000000, 0b000000000, 0b001000001, 0b001001101, 0b001001101, 0b000000000, 0b001001001, 0b000000000, 0b000000000),
            arrayOf(0b001000011, 0b000000000, 0b000000000, 0b001001011, 0b011001011, 0b010001000, 0b000000000, 0b000000000, 0b001001001),
            arrayOf(0b001010101, 0b000000000, 0b001010101, 0b000000000, 0b011011101, 0b010011100, 0b001001000, 0b000000000, 0b011001000),
            arrayOf(0b001010001, 0b000000000, 0b001010001, 0b001011001, 0b001011001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b011000100, 0b010000100, 0b000000000, 0b000000000, 0b011000000)
        ), solver.candidates)
    }

    @Test
    fun nakedPairTest() {
        val sudoku1 = arrayOf<Array<String?>>(
            arrayOf("4",    null,    null,    null,    null,    null,    "9",    "3",    "8"),
            arrayOf(null,    "3",    "2",    null,    "9",    "4",    "1",    null,    null),
            arrayOf(null,    "9",    "5",    "3",    null,    null,    "2",    "4",    null),
            arrayOf("3",    "7",    null,    "6",    null,    "9",    null,    null,    "4"),
            arrayOf("5",    "2",    "9",    null,    null,    "1",    "6",    "7",    "3"),
            arrayOf("6",    null,    "4",    "7",    null,    "3",    null,    "9",    null),
            arrayOf("9",    "5",    "7",    null,    null,    "8",    "3",    null,    null),
            arrayOf(null,    null,    "3",    "9",    null,    null,    "4",    null,    null),
            arrayOf("2",    "4",    null,    null,    "3",    null,    "7",    null,    "9")
        )
        val solver1 = SudokuSolver(sudoku1)
        assertEquals(500, solver1.nakedPair())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000100001, 0b000100001, 0b000010010, 0b001010010, 0b001010010, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b011000000, 0b000000000, 0b000000000, 0b010010000, 0b000000000, 0b000000000, 0b000000000, 0b000110000, 0b001110000),
            arrayOf(0b011000001, 0b000000000, 0b000000000, 0b000000000, 0b011100001, 0b001100000, 0b000000000, 0b000000000, 0b001100000),
            arrayOf(0b000000000, 0b000000000, 0b010000001, 0b000000000, 0b010010010, 0b000000000, 0b010010000, 0b010010011, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b010001000, 0b010001000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010000001, 0b000000000, 0b000000000, 0b010010010, 0b000000000, 0b010010000, 0b000000000, 0b000010011),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000001011, 0b000101011, 0b000000000, 0b000000000, 0b000100011, 0b000100011),
            arrayOf(0b010000001, 0b010100001, 0b000000000, 0b000000000, 0b001110011, 0b001110010, 0b000000000, 0b010110011, 0b000110011),
            arrayOf(0b000000000, 0b000000000, 0b010100001, 0b000010001, 0b000000000, 0b000110000, 0b000000000, 0b010110001, 0b000000000)
        ), solver1.candidates)

        val sudoku2 = arrayOf<Array<String?>>(
            arrayOf(null,    "8",    null,    null,    "9",    null,    null,    "3",    null),
            arrayOf(null,    "3",    null,    null,    null,    null,    null,    "6",    "9"),
            arrayOf("9",    null,    "2",    null,    "6",    "3",    "1",    "5",    "8"),
            arrayOf(null,    "2",    null,    "8",    null,    "4",    "5",    "9",    null),
            arrayOf("8",    "5",    "1",    "9",    null,    "7",    null,    "4",    "6"),
            arrayOf("3",    "9",    "4",    "6",    null,    "5",    "8",    "7",    null),
            arrayOf("5",    "6",    "3",    null,    "4",    null,    "9",    "8",    "7"),
            arrayOf("2",    null,    null,    null,    null,    null,    null,    "1",    "5"),
            arrayOf(null,    "1",    null,    null,    "5",    null,    null,    "2",    null)
        )
        val solver2 = SudokuSolver(sudoku2)
        assertEquals(500, solver2.nakedPair())
        assertEquals(500, solver2.nakedPair())
        assertEquals(500, solver2.nakedPair())
        assertEquals(500, solver2.nakedPair())
        assertArrayEquals(arrayOf(
            arrayOf(0b001101001, 0b000000000, 0b001110000, 0b000011011, 0b000000000, 0b000000011, 0b001001010, 0b000000000, 0b000001010),
            arrayOf(0b001001001, 0b000000000, 0b001010000, 0b000011011, 0b011000011, 0b010000000, 0b001001010, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b001001000, 0b000000000, 0b000001000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b001100000, 0b000000000, 0b001100000, 0b000000000, 0b000000101, 0b000000000, 0b000000000, 0b000000000, 0b000000101),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000110, 0b000000000, 0b000000110, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000011, 0b000000000, 0b000000000, 0b000000000, 0b000000011),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000011, 0b000000000, 0b000000011, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b001001000, 0b110000000, 0b001000100, 0b010000000, 0b110100000, 0b000101100, 0b000000000, 0b000000000),
            arrayOf(0b001001000, 0b000000000, 0b110000000, 0b001000100, 0b000000000, 0b110100000, 0b000101100, 0b000000000, 0b000001100)
        ), solver2.candidates)
    }

    @Test
    fun hiddenPairTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("7",    "2",    null,    "4",    null,    "8",    null,    "3",    null),
            arrayOf(null,    "8",    null,    null,    null,    null,    null,    "4",    "7"),
            arrayOf("4",    null,    "1",    null,    "7",    "6",    "8",    null,    "2"),
            arrayOf("8",    "1",    null,    "7",    "3",    "9",    null,    null,    null),
            arrayOf(null,    null,    null,    "8",    "5",    "1",    null,    null,    null),
            arrayOf(null,    null,    null,    "2",    "6",    "4",    null,    "8",    null),
            arrayOf("2",    null,    "9",    "6",    "8",    null,    "4",    "1",    "3"),
            arrayOf("3",    "4",    null,    null,    null,    null,    null,    null,    "8"),
            arrayOf("1",    "6",    "8",    "9",    "4",    "3",    "2",    "7",    "5")
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(1200, solver.hiddenPair())
        assertEquals(1200, solver.hiddenPair())
        assertEquals(1200, solver.hiddenPair())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000000000, 0b000110000, 0b000000000, 0b100000001, 0b000000000, 0b100110001, 0b000000000, 0b100100001),
            arrayOf(0b100110000, 0b000000000, 0b000110100, 0b000010101, 0b100000011, 0b000010010, 0b100110001, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b100010100, 0b000000000, 0b000010100, 0b000000000, 0b000000000, 0b000000000, 0b100010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000001010, 0b000000000, 0b000000000, 0b000000000, 0b000110000, 0b000110010, 0b000101000),
            arrayOf(0b100100000, 0b001000100, 0b000001010, 0b000000000, 0b000000000, 0b000000000, 0b001000100, 0b100100010, 0b100101000),
            arrayOf(0b100010000, 0b101010100, 0b001010100, 0b000000000, 0b000000000, 0b000000000, 0b001000100, 0b000000000, 0b100000001),
            arrayOf(0b000000000, 0b001010000, 0b000000000, 0b000000000, 0b000000000, 0b001010000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b001010000, 0b000010001, 0b000000011, 0b001010010, 0b100100000, 0b100100000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000)
        ), solver.candidates)
    }

    @Test
    fun nakedTripleTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("2",    "9",    "4",    "5",    "1",    "3",    null,    null,    "6"),
            arrayOf("6",    null,    null,    "8",    "4",    "2",    "3",    "1",    "9"),
            arrayOf("3",    null,    null,    "6",    "9",    "7",    "2",    "5",    "4"),
            arrayOf(null,    null,    null,    null,    "5",    "6",    null,    null,    null),
            arrayOf(null,    "4",    null,    null,    "8",    null,    null,    "6",    null),
            arrayOf(null,    null,    null,    "4",    "7",    null,    null,    null,    null),
            arrayOf("7",    "3",    null,    "1",    "6",    "4",    null,    null,    "5"),
            arrayOf("9",    null,    null,    "7",    "3",    "5",    null,    null,    "1"),
            arrayOf("4",    null,    null,    "9",    "2",    "8",    "6",    "3",    "7")
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(1400, solver.nakedTriple())
        assertEquals(1400, solver.nakedTriple())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b011000000, 0b011000000, 0b000000000),
            arrayOf(0b000000000, 0b001010000, 0b001010000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010000001, 0b010000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b010000001, 0b001000010, 0b101000110, 0b000000110, 0b000000000, 0b000000000, 0b101001001, 0b101001000, 0b010000110),
            arrayOf(0b000010001, 0b000000000, 0b101000110, 0b000000110, 0b000000000, 0b100000001, 0b101010001, 0b000000000, 0b000000110),
            arrayOf(0b010010001, 0b000100010, 0b100100110, 0b000000000, 0b000000000, 0b100000001, 0b100010001, 0b100000000, 0b010000110),
            arrayOf(0b000000000, 0b000000000, 0b010000010, 0b000000000, 0b000000000, 0b000000000, 0b110000000, 0b110000010, 0b000000000),
            arrayOf(0b000000000, 0b010100010, 0b010100010, 0b000000000, 0b000000000, 0b000000000, 0b010001000, 0b010001010, 0b000000000),
            arrayOf(0b000000000, 0b000010001, 0b000010001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000)
        ), solver.candidates)
    }

    @Test
    fun hiddenTripleTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf(null,    null,    null,    null,    null,    "1",    null,    "3",    null),
            arrayOf("2",    "3",    "1",    null,    "9",    null,    null,    null,    null),
            arrayOf(null,    "6",    "5",    null,    null,    "3",    "1",    null,    null),
            arrayOf("6",    "7",    "8",    "9",    "2",    "4",    "3",    null,    null),
            arrayOf("1",    null,    "3",    null,    "5",    null,    null,    null,    "6"),
            arrayOf(null,    null,    null,    "1",    "3",    "6",    "7",    null,    null),
            arrayOf(null,    null,    "9",    "3",    "6",    null,    "5",    "7",    null),
            arrayOf(null,    null,    "6",    null,    "1",    "9",    "8",    "4",    "3"),
            arrayOf("3",    null,    null,    null,    null,    null,    null,    null,    null)
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(1600, solver.hiddenTriple())
        assertEquals(1600, solver.hiddenTriple())
        assertEquals(1600, solver.hiddenTriple())
        assertArrayEquals(arrayOf(
            arrayOf(0b111001000, 0b110001000, 0b001001000, 0b000110010, 0b011001000, 0b000000000, 0b000100010, 0b000000000, 0b000010010),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b011111000, 0b000000000, 0b011010000, 0b000101000, 0b010110000, 0b011001000),
            arrayOf(0b111001000, 0b000000000, 0b000000000, 0b011001010, 0b011001000, 0b000000000, 0b000000000, 0b110000010, 0b011001000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000010001, 0b000010001),
            arrayOf(0b000000000, 0b100001010, 0b000000000, 0b011000000, 0b000000000, 0b011000000, 0b100001010, 0b100000010, 0b000000000),
            arrayOf(0b100011000, 0b100011010, 0b000001010, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b110010010, 0b010001000),
            arrayOf(0b010001000, 0b010001011, 0b000000000, 0b000000000, 0b000000000, 0b010000010, 0b000000000, 0b000000000, 0b000000011),
            arrayOf(0b001010000, 0b000010010, 0b000000000, 0b001010010, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010011011, 0b001001010, 0b011011010, 0b011001000, 0b011010010, 0b100100010, 0b100100011, 0b100000011)
        ), solver.candidates)
    }

    @Test
    fun xWingTest() {
        val sudokuVert = arrayOf<Array<String?>>(
            arrayOf("1",    null,    null,    null,    null,    null,    "5",    "6",    "9"),
            arrayOf("4",    "9",    "2",    null,    "5",    "6",    "1",    null,    "8"),
            arrayOf(null,    "5",    "6",    "1",    null,    "9",    "2",    "4",    null),
            arrayOf(null,    null,    "9",    "6",    "4",    null,    "8",    null,    "1"),
            arrayOf(null,    "6",    "4",    null,    "1",    null,    null,    null,    null),
            arrayOf("2",    "1",    "8",    null,    "3",    "5",    "6",    null,    "4"),
            arrayOf(null,    "4",    null,    "5",    null,    null,    null,    "1",    "6"),
            arrayOf("9",    null,    "5",    null,    "6",    "1",    "4",    null,    "2"),
            arrayOf("6",    "2",    "1",    null,    null,    null,    null,    null,    "5")
        )
        val solverVert = SudokuSolver(sudokuVert)
        assertEquals(1600, solverVert.xWing())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b011000100, 0b001000100, 0b010001110, 0b011000010, 0b011001110, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b001000100, 0b000000000, 0b000000000, 0b000000000, 0b001000100, 0b000000000),
            arrayOf(0b011000100, 0b000000000, 0b000000000, 0b000000000, 0b011000000, 0b000000000, 0b000000000, 0b000000000, 0b001000100),
            arrayOf(0b001010100, 0b001000100, 0b000000000, 0b000000000, 0b000000000, 0b001000010, 0b000000000, 0b000010110, 0b000000000),
            arrayOf(0b001010100, 0b000000000, 0b000000000, 0b110000010, 0b000000000, 0b011000010, 0b101000100, 0b100010110, 0b001000100),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b101000000, 0b000000000, 0b000000000, 0b000000000, 0b101000000, 0b000000000),
            arrayOf(0b011000100, 0b000000000, 0b001000100, 0b000000000, 0b111000010, 0b011000110, 0b101000100, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b011000100, 0b000000000, 0b010000100, 0b000000000, 0b000000000, 0b000000000, 0b010000100, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b110001100, 0b111000000, 0b011001100, 0b101000100, 0b110000100, 0b000000000)
        ), solverVert.candidates)

        val sudokuHor = arrayOf<Array<String?>>(
            arrayOf(null,    null,    null,    null,    null,    null,    null,    "9",    "4"),
            arrayOf("7",    "6",    null,    "9",    "1",    null,    null,    "5",    null),
            arrayOf(null,    "9",    null,    null,    null,    "2",    null,    "8",    "1"),
            arrayOf(null,    "7",    null,    null,    "5",    null,    null,    "1",    null),
            arrayOf(null,    null,    null,    "7",    null,    "9",    null,    null,    null),
            arrayOf(null,    "8",    null,    null,    "3",    "1",    null,    "6",    "7"),
            arrayOf("2",    "4",    null,    "1",    null,    null,    null,    "7",    null),
            arrayOf(null,    "1",    null,    null,    "9",    null,    null,    "4",    "5"),
            arrayOf("9",    null,    null,    null,    null,    null,    "1",    null,    null)
        )
        val solverHor = SudokuSolver(sudokuHor)
        assertEquals(1600, solverHor.xWing())
        assertArrayEquals(arrayOf(
            arrayOf(0b010010101, 0b000010110, 0b010010111, 0b010110100, 0b011100000, 0b011110100, 0b001100110, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b010001110, 0b000000000, 0b000000000, 0b010001100, 0b000000110, 0b000000000, 0b000000110),
            arrayOf(0b000011100, 0b000000000, 0b000011100, 0b000111100, 0b001101000, 0b000000000, 0b001100100, 0b000000000, 0b000000000),
            arrayOf(0b000101100, 0b000000000, 0b100101110, 0b010101010, 0b000000000, 0b010101000, 0b110001110, 0b000000000, 0b110000110),
            arrayOf(0b000111101, 0b000010100, 0b000111101, 0b000000000, 0b010101010, 0b000000000, 0b010011100, 0b000000110, 0b010000100),
            arrayOf(0b000011000, 0b000000000, 0b100011010, 0b000001010, 0b000000000, 0b000000000, 0b100011010, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b010110100, 0b000000000, 0b010100000, 0b010110100, 0b110100100, 0b000000000, 0b110100100),
            arrayOf(0b010100100, 0b000000000, 0b011100100, 0b010100110, 0b000000000, 0b011100100, 0b010100110, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000010100, 0b011110100, 0b010111100, 0b011101010, 0b011111100, 0b000000000, 0b000000110, 0b010100100)
        ), solverHor.candidates)
    }

    @Test
    fun yWingTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    null,    null,    "2",    "4",    null,    null,    null,    null),
            arrayOf(null,    "5",    null,    "6",    "9",    null,    "2",    "3",    "1"),
            arrayOf(null,    "2",    null,    null,    "5",    null,    null,    "9",    null),
            arrayOf(null,    "9",    null,    "7",    null,    null,    "3",    "2",    null),
            arrayOf(null,    null,    "2",    "9",    "3",    "5",    "6",    null,    "7"),
            arrayOf(null,    "7",    null,    null,    null,    "2",    "9",    null,    null),
            arrayOf(null,    "6",    "9",    null,    "2",    null,    null,    "7",    "3"),
            arrayOf("5",    "1",    null,    null,    "7",    "9",    null,    "6",    "2"),
            arrayOf("2",    null,    "7",    null,    "8",    "6",    null,    null,    "9")
        )
        val solver = SudokuSolver(sudoku)
        solver.nakedPair()
        solver.candidateLines()
        solver.candidateLines()
        solver.candidateLines()
        solver.candidateLines()
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b010000100, 0b010100101, 0b000000000, 0b000000000, 0b011000101, 0b001010000, 0b010010000, 0b010110000),
            arrayOf(0b011001000, 0b000000000, 0b010001000, 0b000000000, 0b000000000, 0b011000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b011100101, 0b000000000, 0b010100101, 0b010000001, 0b000000000, 0b011000101, 0b001001000, 0b000000000, 0b010101000),
            arrayOf(0b010101001, 0b000000000, 0b010111001, 0b000000000, 0b000100001, 0b010001000, 0b000000000, 0b000000000, 0b010011000),
            arrayOf(0b010001001, 0b010001000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b010001001, 0b000000000),
            arrayOf(0b010101101, 0b000000000, 0b010111101, 0b010001000, 0b000100001, 0b000000000, 0b000000000, 0b010011001, 0b010011000),
            arrayOf(0b010001000, 0b000000000, 0b000000000, 0b000011001, 0b000000000, 0b000001001, 0b010011001, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b010001100, 0b000001100, 0b000000000, 0b000000000, 0b010001000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000001100, 0b000000000, 0b000011101, 0b000000000, 0b000000000, 0b000011001, 0b000011000, 0b000000000)
        ), solver.candidates)

        assertEquals(2500, solver.yWing())
        assertEquals(2500, solver.yWing())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b010000100, 0b010100101, 0b000000000, 0b000000000, 0b011000101, 0b001010000, 0b010010000, 0b010110000),
            arrayOf(0b011001000, 0b000000000, 0b010001000, 0b000000000, 0b000000000, 0b011000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b011100101, 0b000000000, 0b010100101, 0b010000001, 0b000000000, 0b011000101, 0b001001000, 0b000000000, 0b010101000),
            arrayOf(0b010101001, 0b000000000, 0b010111001, 0b000000000, 0b000100001, 0b010001000, 0b000000000, 0b000000000, 0b010011000),
            arrayOf(0b010001001, 0b010001000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b010001001, 0b000000000),
            arrayOf(0b010101101, 0b000000000, 0b010111101, 0b010001000, 0b000100001, 0b000000000, 0b000000000, 0b010011001, 0b010011000),
            arrayOf(0b010001000, 0b000000000, 0b000000000, 0b000010001, 0b000000000, 0b000000001, 0b010011001, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b010000100, 0b000001100, 0b000000000, 0b000000000, 0b010001000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000001100, 0b000000000, 0b000011101, 0b000000000, 0b000000000, 0b000011001, 0b000011000, 0b000000000)
        ), solver.candidates)
    }

    @Test
    fun singleChainsTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf(null,    null,    "7",    null,    "8",    "3",    "6",    null,    null),
            arrayOf(null,    "3",    "9",    "7",    null,    "6",    "8",    null,    null),
            arrayOf("8",    "2",    "6",    "4",    "1",    "9",    "7",    "5",    "3"),
            arrayOf("6",    "4",    null,    "1",    "9",    null,    "3",    "8",    "7"),
            arrayOf(null,    "8",    null,    "3",    "6",    "7",    null,    null,    null),
            arrayOf(null,    "7",    "3",    null,    "4",    "8",    null,    "6",    null),
            arrayOf("3",    "9",    null,    "8",    "7",    null,    null,    "2",    "6"),
            arrayOf("7",    "6",    "4",    "9",    null,    null,    "1",    "3",    "8"),
            arrayOf("2",    null,    "8",    "6",    "3",    null,    "9",    "7",    null)
        )
        val solver = SudokuSolver(sudoku)

        solver.nakedPair()
        solver.nakedPair()
        solver.candidateLines()
        assertArrayEquals(arrayOf(
            arrayOf(0b000011001, 0b000010001, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000000000, 0b100001001, 0b100001011),
            arrayOf(0b000011001, 0b000000000, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000001001, 0b000001011),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b100010001, 0b000000000, 0b000010011, 0b000000000, 0b000000000, 0b000000000, 0b000011010, 0b100001001, 0b100011001),
            arrayOf(0b100000001, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b100000001),
            arrayOf(0b000000000, 0b000000000, 0b000010001, 0b000000000, 0b000000000, 0b000001001, 0b000011000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000010010, 0b000010010, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000010001, 0b000000000, 0b000000000, 0b000000000, 0b000001001, 0b000000000, 0b000000000, 0b000011000)
        ), solver.candidates)

        assertEquals(3000, solver.singleChains())
        assertArrayEquals(arrayOf(
            arrayOf(0b000011001, 0b000010001, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000000000, 0b100001001, 0b100001011),
            arrayOf(0b000011001, 0b000000000, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000001001, 0b000001011),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b100010001, 0b000000000, 0b000000011, 0b000000000, 0b000000000, 0b000000000, 0b000011010, 0b100001001, 0b100011001),
            arrayOf(0b100000001, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b000000000, 0b000010010, 0b000000000, 0b100000001),
            arrayOf(0b000000000, 0b000000000, 0b000010001, 0b000000000, 0b000000000, 0b000001001, 0b000011000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000010010, 0b000010010, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000010001, 0b000000000, 0b000000000, 0b000000000, 0b000001001, 0b000000000, 0b000000000, 0b000011000)
        ), solver.candidates)
        assertEquals(3000, solver.singleChains())
        assertArrayEquals(arrayOf<Array<String?>>(
            arrayOf(null,    null,    "7",    null,    "8",    "3",    "6",    null,    null),
            arrayOf(null,    "3",    "9",    "7",    "5",    "6",    "8",    null,    null),
            arrayOf("8",    "2",    "6",    "4",    "1",    "9",    "7",    "5",    "3"),
            arrayOf("6",    "4",    "5",    "1",    "9",    null,    "3",    "8",    "7"),
            arrayOf(null,    "8",    null,    "3",    "6",    "7",    null,    null,    "5"),
            arrayOf(null,    "7",    "3",    "5",    "4",    "8",    null,    "6",    null),
            arrayOf("3",    "9",    null,    "8",    "7",    null,    "5",    "2",    "6"),
            arrayOf("7",    "6",    "4",    "9",    null,    "5",    "1",    "3",    "8"),
            arrayOf("2",    "5",    "8",    "6",    "3",    null,    "9",    "7",    null)
        ), sudoku)
    }

    @Test
    fun nakedQuadTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf(null,    null,    null,    null,    "3",    null,    null,    "8",    "6"),
            arrayOf(null,    null,    null,    null,    "2",    null,    null,    "4",    null),
            arrayOf(null,    "9",    null,    null,    "7",    "8",    "5",    "2",    null),
            arrayOf("3",    "7",    "1",    "8",    "5",    "6",    "2",    "9",    "4"),
            arrayOf("9",    null,    null,    "1",    "4",    "2",    "3",    "7",    "5"),
            arrayOf("4",    null,    null,    "3",    "9",    "7",    "6",    "1",    "8"),
            arrayOf("2",    null,    null,    "7",    null,    "3",    "8",    "5",    "9"),
            arrayOf(null,    "3",    "9",    "2",    null,    "5",    "4",    "6",    "7"),
            arrayOf("7",    null,    null,    "9",    null,    "4",    "1",    "3",    "2")
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(4000, solver.nakedQuad())
        assertArrayEquals(arrayOf(
            arrayOf(0b000010001, 0b000001010, 0b001001010, 0b000011000, 0b000000000, 0b100000001, 0b101000000, 0b000000000, 0b000000000),
            arrayOf(0b010110001, 0b010110001, 0b001000100, 0b000110000, 0b000000000, 0b100000001, 0b101000000, 0b000000000, 0b000000101),
            arrayOf(0b000100001, 0b000000000, 0b000001100, 0b000101000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000101),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010100000, 0b010100000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000010010, 0b000010010, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000101001, 0b000101000, 0b000000000, 0b000100001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b010000001, 0b000000000, 0b000000000, 0b000000000, 0b010000001, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010110000, 0b010110000, 0b000000000, 0b010100000, 0b000000000, 0b000000000, 0b000000000, 0b000000000)
        ), solver.candidates)
    }

    @Test
    fun hiddenQuadTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    null,    "1",    "5",    null,    null,    null,    "4",    "6"),
            arrayOf("4",    "2",    "5",    null,    "9",    null,    null,    "8",    "1"),
            arrayOf("8",    "6",    null,    null,    "1",    null,    null,    "2",    null),
            arrayOf("5",    null,    "2",    null,    null,    null,    null,    null,    null),
            arrayOf(null,    "1",    "9",    null,    null,    null,    "4",    "6",    null),
            arrayOf("6",    null,    null,    null,    null,    null,    null,    null,    "2"),
            arrayOf("1",    "9",    "6",    null,    "4",    null,    "2",    "5",    "3"),
            arrayOf("2",    null,    null,    null,    "6",    null,    "8",    "1",    "7"),
            arrayOf(null,    null,    null,    null,    null,    "1",    "6",    "9",    "4")
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(5000, solver.hiddenQuad())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b001000100, 0b000000000, 0b000000000, 0b011000110, 0b011000110, 0b001000100, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b001100100, 0b000000000, 0b001100100, 0b001000100, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b001000100, 0b001001100, 0b000000000, 0b001001100, 0b101010100, 0b000000000, 0b100010000),
            arrayOf(0b000000000, 0b011001100, 0b000000000, 0b100101001, 0b011000100, 0b100101000, 0b101000101, 0b001000100, 0b110000000),
            arrayOf(0b001000100, 0b000000000, 0b000000000, 0b011000110, 0b011010110, 0b011010110, 0b000000000, 0b000000000, 0b010010000),
            arrayOf(0b000000000, 0b011001100, 0b011001100, 0b100001001, 0b011010100, 0b100001000, 0b101010101, 0b001000100, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b011000000, 0b000000000, 0b011000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000011100, 0b000001100, 0b100000100, 0b000000000, 0b100010100, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b001000100, 0b011010100, 0b011000100, 0b011000110, 0b011010110, 0b000000000, 0b000000000, 0b000000000, 0b000000000)
        ), solver.candidates)
    }

    @Test
    fun swordfishTest() {
        val sudokuRow = arrayOf<Array<String?>>(
            arrayOf(null,    "2",    null,    null,    "4",    "3",    null,    "6",    "9"),
            arrayOf(null,    null,    "3",    "8",    "9",    "6",    "2",    null,    null),
            arrayOf("9",    "6",    null,    null,    "2",    "5",    null,    "3",    null),
            arrayOf("8",    "9",    null,    "5",    "6",    null,    null,    "1",    "3"),
            arrayOf("6",    null,    null,    null,    "3",    null,    null,    null,    null),
            arrayOf(null,    "3",    null,    null,    "8",    "1",    null,    "2",    "6"),
            arrayOf("3",    null,    null,    null,    "1",    null,    null,    "7",    null),
            arrayOf(null,    null,    "9",    "6",    "7",    "4",    "3",    null,    "2"),
            arrayOf("2",    "7",    null,    "3",    "5",    "8",    null,    "9",    null)
        )
        val solverRow = SudokuSolver(sudokuRow)
        solverRow.nakedPair()
        solverRow.candidateLines()
        solverRow.candidateLines()
        assertArrayEquals(arrayOf(
            arrayOf(0b001010001, 0b000000000, 0b011010001, 0b001000001, 0b000000000, 0b000000000, 0b011010001, 0b000000000, 0b000000000),
            arrayOf(0b001011001, 0b000011001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000011000, 0b001011001),
            arrayOf(0b000000000, 0b000000000, 0b011001001, 0b001000001, 0b000000000, 0b000000000, 0b011001001, 0b000000000, 0b011001001),
            arrayOf(0b000000000, 0b000000000, 0b001001010, 0b000000000, 0b000000000, 0b001000010, 0b001001000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000011001, 0b001011011, 0b100001010, 0b000000000, 0b101000010, 0b111011000, 0b010011000, 0b011011000),
            arrayOf(0b001011000, 0b000000000, 0b001011000, 0b100001000, 0b000000000, 0b000000000, 0b101011000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010011000, 0b000111000, 0b100000010, 0b000000000, 0b100000010, 0b010111000, 0b000000000, 0b010011000),
            arrayOf(0b000010001, 0b010010001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b010010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000101000, 0b000000000, 0b000000000, 0b000000000, 0b000101001, 0b000000000, 0b000001001)
        ), solverRow.candidates)
        assertEquals(6000, solverRow.swordfish())
        assertArrayEquals(arrayOf(
            arrayOf(0b001010001, 0b000000000, 0b011010001, 0b001000001, 0b000000000, 0b000000000, 0b011010001, 0b000000000, 0b000000000),
            arrayOf(0b001011001, 0b000011001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000011000, 0b001010001),
            arrayOf(0b000000000, 0b000000000, 0b011001001, 0b001000001, 0b000000000, 0b000000000, 0b011001001, 0b000000000, 0b011001001),
            arrayOf(0b000000000, 0b000000000, 0b001001010, 0b000000000, 0b000000000, 0b001000010, 0b001001000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000011001, 0b001010011, 0b100001010, 0b000000000, 0b101000010, 0b111010000, 0b010011000, 0b011010000),
            arrayOf(0b001011000, 0b000000000, 0b001010000, 0b100001000, 0b000000000, 0b000000000, 0b101010000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b010011000, 0b000110000, 0b100000010, 0b000000000, 0b100000010, 0b010110000, 0b000000000, 0b010010000),
            arrayOf(0b000010001, 0b010010001, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b010010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000101000, 0b000000000, 0b000000000, 0b000000000, 0b000101001, 0b000000000, 0b000001001)
        ), solverRow.candidates)

        val sudokuCol = arrayOf<Array<String?>>(
            arrayOf("9",    "2",    "6",    null,    null,    null,    "1",    null,    null),
            arrayOf("5",    "3",    "7",    null,    "1",    null,    "4",    "2",    null),
            arrayOf("8",    "4",    "1",    null,    null,    null,    "6",    null,    "3"),
            arrayOf("2",    "5",    "9",    "7",    "3",    "4",    "8",    "1",    "6"),
            arrayOf("7",    "1",    "4",    null,    "6",    null,    null,    "3",    null),
            arrayOf("3",    "6",    "8",    "1",    "2",    null,    null,    "4",    null),
            arrayOf("1",    null,    "2",    null,    null,    null,    null,    "8",    "4"),
            arrayOf("4",    "8",    "5",    null,    "7",    "1",    "3",    "6",    null),
            arrayOf("6",    null,    "3",    null,    null,    null,    null,    null,    "1")
        )
        val solverCol = SudokuSolver(sudokuCol)
        solverCol.nakedPair()
        solverCol.nakedTriple()
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b010011100, 0b010001000, 0b011010100, 0b000000000, 0b001010000, 0b011010000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b110100000, 0b000000000, 0b110100000, 0b000000000, 0b000000000, 0b110000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b100010010, 0b100010000, 0b101010010, 0b000000000, 0b101010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b110010000, 0b000000000, 0b110010000, 0b100010010, 0b000000000, 0b100010010),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b100010000, 0b101010000, 0b000000000, 0b101010000),
            arrayOf(0b000000000, 0b101000000, 0b000000000, 0b000100100, 0b100010000, 0b000100100, 0b101010000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b100000010, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b100000010),
            arrayOf(0b000000000, 0b101000000, 0b000000000, 0b110011010, 0b010001000, 0b110010010, 0b101010010, 0b101010000, 0b000000000)
        ), solverCol.candidates)
        assertEquals(6000, solverCol.swordfish())
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b010011100, 0b010001000, 0b011010100, 0b000000000, 0b001010000, 0b011010000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b110100000, 0b000000000, 0b110100000, 0b000000000, 0b000000000, 0b110000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000010010, 0b100010000, 0b001010010, 0b000000000, 0b101010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b110010000, 0b000000000, 0b110010000, 0b100010010, 0b000000000, 0b100010010),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b100010000, 0b101010000, 0b000000000, 0b101010000),
            arrayOf(0b000000000, 0b101000000, 0b000000000, 0b000100100, 0b100010000, 0b000100100, 0b001010000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b100000010, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b100000010),
            arrayOf(0b000000000, 0b101000000, 0b000000000, 0b010011010, 0b010001000, 0b010010010, 0b001010010, 0b101010000, 0b000000000)
        ), solverCol.candidates)
    }

    @Test
    fun BUGTest() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("1",    "4",    "2",    "8",    "9",    "5",    "7",    "6",    "3"),
            arrayOf(null,    null,    "6",    "2",    null,    "3",    null,    "1",    null),
            arrayOf(null,    null,    "3",    "6",    "1",    null,    null,    null,    "2"),
            arrayOf("8",    "6",    "7",    "1",    "2",    "9",    "3",    null,    null),
            arrayOf("3",    "5",    "1",    "4",    null,    null,    "8",    "2",    "9"),
            arrayOf("9",    "2",    "4",    "5",    "3",    "8",    "6",    "7",    "1"),
            arrayOf("6",    "7",    null,    "3",    null,    "1",    "2",    "9",    null),
            arrayOf("2",    "3",    null,    "9",    null,    null,    "1",    null,    "7"),
            arrayOf("4",    "1",    "9",    "7",    "8",    "2",    "5",    "3",    "6")
        )
        val solver = SudokuSolver(sudoku)
        assertEquals(0, solver.BUG())
        solver.nakedPair()
        assertArrayEquals(arrayOf(
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b001010000, 0b110000000, 0b000000000, 0b000000000, 0b001001000, 0b000000000, 0b100001000, 0b000000000, 0b010010000),
            arrayOf(0b001010000, 0b110000000, 0b000000000, 0b000000000, 0b000000000, 0b001001000, 0b100001000, 0b010010000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000011000, 0b000011000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b001100000, 0b001100000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b010010000, 0b000000000, 0b000011000, 0b000000000, 0b000000000, 0b000000000, 0b010001000),
            arrayOf(0b000000000, 0b000000000, 0b010010000, 0b000000000, 0b000111000, 0b000101000, 0b000000000, 0b010001000, 0b000000000),
            arrayOf(0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000, 0b000000000)
        ), solver.candidates)
        assertEquals(6500, solver.BUG())
        assertArrayEquals(arrayOf<Array<String?>>(
            arrayOf("1",    "4",    "2",    "8",    "9",    "5",    "7",    "6",    "3"),
            arrayOf(null,    null,    "6",    "2",    null,    "3",    null,    "1",    null),
            arrayOf(null,    null,    "3",    "6",    "1",    null,    null,    null,    "2"),
            arrayOf("8",    "6",    "7",    "1",    "2",    "9",    "3",    null,    null),
            arrayOf("3",    "5",    "1",    "4",    null,    null,    "8",    "2",    "9"),
            arrayOf("9",    "2",    "4",    "5",    "3",    "8",    "6",    "7",    "1"),
            arrayOf("6",    "7",    null,    "3",    null,    "1",    "2",    "9",    null),
            arrayOf("2",    "3",    null,    "9",    "4",    null,    "1",    null,    "7"),
            arrayOf("4",    "1",    "9",    "7",    "8",    "2",    "5",    "3",    "6")
        ), sudoku)
    }
}