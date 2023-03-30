package com.example.sudoku

class SudokuUtil {
    companion object {
        inline fun<reified T> getRelevantValues(grid: Array<Array<T>>, row: Int, col: Int): Array<T> {
            val values = mutableListOf<T>()
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            for (i in 0..2) {
                for (j in 0..2) {
                    values.add(grid[fromRow + i][fromCol + j])
                }
            }

            for (i in 0 until fromCol) values.add(grid[row][i])
            for (i in (fromCol + 3) until grid.size) values.add(grid[row][i])

            for (i in 0 until fromRow) values.add(grid[i][col])
            for (i in (fromRow + 3) until grid[0].size) values.add(grid[i][col])

            return values.toTypedArray()
        }

        fun getRelevantCoords(row: Int, col: Int): Array<Pair<Int, Int>> {
            val values = mutableListOf<Pair<Int, Int>>()
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            for (i in 0..2) {
                for (j in 0..2) {
                    values.add(Pair(fromRow + i, fromCol + j))
                }
            }

            for (i in 0 until fromCol) values.add(Pair(row, i))
            for (i in (fromCol + 3) ..8) values.add(Pair(row, i))

            for (i in 0 until fromRow) values.add(Pair(i, col))
            for (i in (fromRow + 3) ..8) values.add(Pair(i, col))

            return values.toTypedArray()
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

            return Array(9) {
                grid[fromRow + kotlin.math.floor(it / 3.0).toInt()][fromCol + (it % 3)]
            }
        }

        inline fun<reified T> getSquare(grid: Array<Array<T>>, square: Int): Array<T> {
            val fromRow = (kotlin.math.floor(square / 3.0) * 3).toInt()
            val fromCol = (square % 3) * 3

            return Array(9) {
                grid[fromRow + kotlin.math.floor(it / 3.0).toInt()][fromCol + (it % 3)]
            }
        }

        fun coordsSquareConversion(rowOrSquare: Int, colOrIndex: Int): Pair<Int, Int> {
            return Pair(
                (kotlin.math.floor(rowOrSquare / 3.0) * 3 +
                        kotlin.math.floor(colOrIndex / 3.0)).toInt(),
                (rowOrSquare % 3) * 3 + (colOrIndex % 3)
            )
        }

        inline fun<reified  T> getSquareAsMat(grid: Array<Array<T>>, row: Int, col: Int): Array<Array<T>> {
            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

            return Array(3) { i -> Array(3) { j -> grid[fromRow + i][fromCol + j]} }
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
            for (i in (fromCol + 3) until grid.size) grid[row][i] = transform(grid[row][i])

            for (i in 0 until fromRow) grid[i][col] = transform(grid[i][col])
            for (i in (fromRow + 3)until grid[0].size) grid[i][col] = transform(grid[i][col])
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

        inline fun<reified T> applyToGrid(grid: Array<Array<T>>, transform: (T) -> T) {
            for (row in grid.indices) {
                for (col in grid[0].indices) {
                    grid[row][col] = transform(grid[row][col])
                }
            }
        }
    }
}