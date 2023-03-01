package com.example.sudoku

class SudokuSolver {
    val candidates = Array(9) { Array(9) { MutableList(9) { lit -> (lit + 1).toString() }}}
    var grid: Array<Array<String?>>

    constructor(grid: Array<Array<String?>>) {
        this.grid = grid

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == null) {
                    continue
                }

                candidates[row][col].clear()
                for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
                    cands.remove(grid[row][col])
                }
            }
        }
    }

    fun addNumber(numb: String, row: Int, col: Int) {
        grid[row][col] = numb
        candidates[row][col].clear()
        for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
            cands.remove(numb)
        }
    }

    fun removeNumber(row: Int, col: Int) {
        val temp = grid[row][col]!!
        grid[row][col] = null

        for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
                cands.add(temp)
        }

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] != temp) {
                    continue
                }

                for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
                    cands.remove(temp)
                }
            }
        }

        candidates[row][col] = MutableList(9) { (it + 1).toString() }
        for (numb in SudokuUtil.getRelevantValues(grid, row, col)) {
            if (numb != null) {
                candidates[row][col].remove(numb)
            }
        }
    }

    fun singleCandidatePosition(): Int {
        var difficultyScore = 0

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] != null) {
                    continue
                }

                if (candidates[row][col].size == 0) {
                    throw NoSolutionException()
                }
                if (candidates[row][col].size == 1) {
                    addNumber(candidates[row][col][0], row, col)
                    difficultyScore += 100
                } else {
                    val rowCands = SudokuUtil.getRow(candidates, row).reduce {
                            acc, mls -> (acc + mls).toMutableList() }
                    val colCands = SudokuUtil.getColumn(candidates, col).reduce {
                            acc, mls -> (acc + mls).toMutableList() }
                    val squareCands = SudokuUtil.getSquare(candidates, row, col).reduce {
                            acc, mls -> (acc + mls).toMutableList() }
                    for (numb in candidates[row][col]) {
                        rowCands.remove(numb)
                        colCands.remove(numb)
                        squareCands.remove(numb)
                        if ( numb !in rowCands || numb !in colCands || numb !in squareCands) {
                            addNumber(numb, row, col)
                            difficultyScore += 100
                        }
                    }
                }
            }
        }

        return difficultyScore
    }

    fun candidateLines(): Int {
        var difficultyScore = 0



        return difficultyScore
    }
}