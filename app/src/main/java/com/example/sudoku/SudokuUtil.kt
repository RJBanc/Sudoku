package com.example.sudoku

class SudokuUtil {
    companion object {
        inline fun<reified T> getRelevantValues(grid: Array<Array<T>>, row: Int, col: Int): Array<T> {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            val rowArr = grid[row]
            val colArr = Array(9) {grid[it][col]}
            val square = grid[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()

            return rowArr + colArr + square
        }
    }
}