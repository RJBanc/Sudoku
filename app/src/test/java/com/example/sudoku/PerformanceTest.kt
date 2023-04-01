package com.example.sudoku


import org.junit.Test
import kotlin.system.measureTimeMillis


class PerformanceTest {

    private fun Array<Array<String?>>.copy() = Array(size) { get(it).clone() }

    @Test
    fun sudokuCreationTime() {
        val sudokuLogic = SudokuLogic()
        val elapsed = measureTimeMillis {
            sudokuLogic.makeSolution()
        }
        println(elapsed)
    }

    @Test
    fun solverVsBrute() {
        val sudoku = arrayOf<Array<String?>>(
            arrayOf("9",    "4",    "6",    "1",    "7",    null,    "3",    null,    null),
            arrayOf("2",    null,    null,    "8",    null,    null,    "6",    null,    null),
            arrayOf("7",    "8",    "3",    "6",    null,    null,    null,    null,    null),
            arrayOf(null,    null,    null,    null,    "8",    null,    "5",    null,    null),
            arrayOf(null,    null,    null,    "4",    null,    "9",    null,    null,    null),
            arrayOf("4",    null,    "8",    "2",    "5",    null,    "1",    "6",    null),
            arrayOf(null,    "5",    null,    null,    "2",    null,    null,    "9",    null),
            arrayOf(null,    "7",    null,    "9",    "3",    "4",    null,    "5",    null),
            arrayOf(null,    null,    null,    "5",    null,    "8",    null,    null,    null)
        )

        val sudokuLogic = SudokuLogic()
        val solver = SudokuSolver(sudoku.copy())
        val elapsedBrute = measureTimeMillis {
            sudokuLogic.checkNumSolutions(sudoku.copy())
        }
        val elapsedSolver = measureTimeMillis{
            solver.solve()
        }
        println(elapsedBrute)
        println(elapsedSolver)
    }
}