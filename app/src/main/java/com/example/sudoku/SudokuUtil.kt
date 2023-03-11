package com.example.sudoku

class SudokuUtil {
    companion object {
        inline fun<reified T> getRelevantValues(grid: Array<Array<T>>, row: Int, col: Int): Array<T> {
            return (getRow(grid, row) + getColumn(grid, col) + getSquare(grid, row, col)).toSet().toTypedArray() // dumb hack to remove duplicates (only works if T is class object)
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

        inline fun<reified T> getSquare(grid: Array<Array<T>>, square: Int): Array<T> {
            val fromRow = (kotlin.math.floor(square / 3.0) * 3).toInt()
            val fromCol = (square % 3) * 3

            return grid[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                    grid[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()
        }

        fun getCoordsInSqaure(squareIndex: Int, posIndex: Int): Pair<Int, Int> {
            val row = (kotlin.math.floor(squareIndex / 3.0) * 3).toInt() +
                    (kotlin.math.floor(posIndex / 3.0)).toInt()
            val col = (squareIndex % 3) * 3 + (posIndex % 3)

            return Pair(row, col)
        }

        inline fun<reified  T> getSquareAsMat(grid: Array<Array<T>>, row: Int, col: Int): Array<Array<T>> {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()
            return arrayOf(grid[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray(),
                    grid[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray(),
                    grid[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray())
        }

        inline fun<reified T> applyToRelevantValues(grid: Array<Array<T>>, row: Int, col: Int, transform: (T) -> T) {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            for (i in 0..2) {
                for (j in 0..2) {
                    grid[fromRow + i][fromCol + j] = transform(grid[fromRow + i][fromCol + j])
                }
            }

            for (i in 0 until fromCol) grid[row][i] = transform(grid[row][i])
            for (i in (fromCol + 3)..grid.size) grid[row][i] = transform(grid[row][i])

            for (i in 0 until fromRow) grid[i][col] = transform(grid[i][col])
            for (i in (fromRow + 3)..grid[0].size) grid[i][col] = transform(grid[i][col])
        }

        inline fun<reified T> applyToRow(grid: Array<Array<T>>, row: Int, transform: (T) -> T) {
            for (i in grid[row].indices) grid[row][i] = transform(grid[row][i])
        }

        inline fun<reified T> applyToColumn(grid: Array<Array<T>>, col: Int, transform: (T) -> T) {
            for (i in grid.indices) grid[i][col] = transform(grid[i][col])
        }

        inline fun<reified T> applyToSquare(grid: Array<Array<T>>, row: Int, col: Int, transform: (T) -> T) {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            for (i in 0..2) {
                for (j in 0..2) {
                    grid[fromRow + i][fromCol + j] = transform(grid[fromRow + i][fromCol + j])
                }
            }
        }

        inline fun<reified T> applyToSquare(grid: Array<Array<T>>, square: Int, transform: (T) -> T) {
            val fromRow = (kotlin.math.floor(square / 3.0) * 3).toInt()
            val fromCol = (square % 3) * 3

            for (i in 0..2) {
                for (j in 0..2) {
                    grid[fromRow + i][fromCol + j] = transform(grid[fromRow + i][fromCol + j])
                }
            }
        }
    }
}