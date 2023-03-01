package com.example.sudoku

class SudokuUtil {
    companion object {
        inline fun<reified T> getRelevantValues(grid: Array<Array<T>>, row: Int, col: Int): Array<T> {
            return getRow(grid, row) + getColumn(grid, col) + getSquare(grid, row, col)
        }

        inline fun<reified T> getRow(grid: Array<Array<T>>, row: Int): Array<T> {
            return grid[row]
        }

        inline fun<reified T> getColumn(grid: Array<Array<T>>, col: Int): Array<T> {
            return Array(9) { grid[it][col] }
        }

        inline fun<reified T> getSquare(grid: Array<Array<T>>, row: Int, col: Int): Array<T> {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            return grid[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()
        }
    }
}