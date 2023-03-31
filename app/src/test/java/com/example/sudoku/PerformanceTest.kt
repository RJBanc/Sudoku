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
            arrayOf("3",    null,    "9",    null,    null,    null,    "4",    null,    null),
            arrayOf("2",    null,    null,    "7",    null,    "9",    null,    null,    null),
            arrayOf(null,    "8",    "7",    null,    null,    null,    null,    null,    null),
            arrayOf("7",    "5",    null,    null,    "6",    null,    "2",    "3",    null),
            arrayOf("6",    null,    null,    "9",    null,    "4",    null,    null,    "8"),
            arrayOf(null,    "2",    "8",    null,    "5",    null,    null,    "4",    "1"),
            arrayOf(null,    null,    null,    null,    null,    null,    "5",    "9",    null),
            arrayOf(null,    null,    null,    "1",    null,    "6",    null,    null,    "7"),
            arrayOf(null,    null,    "6",    null,    null,    null,    "1",    null,    "4")
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