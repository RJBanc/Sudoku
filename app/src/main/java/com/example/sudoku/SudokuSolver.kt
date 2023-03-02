package com.example.sudoku

class SudokuSolver {
    private val candidates = Array(9) { Array(9) { 0b111111111 } }
    var grid: Array<Array<String?>>

    private val stringToBitMap = mapOf<String?, Int>(
        "1" to 0b1,
        "2" to 0b10,
        "3" to 0b100,
        "4" to 0b1000,
        "5" to 0b10000,
        "6" to 0b100000,
        "7" to 0b1000000,
        "8" to 0b10000000,
        "9" to 0b100000000
    )
    private val bitToStringMap = mapOf(
        0b1 to "1",
        0b10 to "2",
        0b100 to "3",
        0b1000 to "4",
        0b10000 to "5",
        0b100000 to "6",
        0b1000000 to "7",
        0b10000000 to "8",
        0b100000000 to "9"
    )


    constructor(grid: Array<Array<String?>>) {
        this.grid = grid

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == null) {
                    continue
                }

                candidates[row][col] = 0
                SudokuUtil.applyToRelevantValues(candidates, row, col) {
                    it and stringToBitMap.getValue(grid[row][col]).inv() //remove number as candidate from relevant fields
                }
            }
        }
    }

    fun addNumber(numb: String, row: Int, col: Int) {
        grid[row][col] = numb
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            it and stringToBitMap.getValue(numb).inv()
        }
    }

    fun addNumber(numb: Int, row: Int, col: Int) {
        grid[row][col] = bitToStringMap.getValue(numb)
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            it and numb.inv()
        }
    }

//    fun removeNumber(row: Int, col: Int) {
//        val temp = grid[row][col]!!
//        grid[row][col] = null
//
//        for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
//                cands.add(temp)
//        }
//
//        for (row in 0..8) {
//            for (col in 0..8) {
//                if (grid[row][col] != temp) {
//                    continue
//                }
//
//                for (cands in SudokuUtil.getRelevantValues(candidates, row, col)) {
//                    cands.remove(temp)
//                }
//            }
//        }
//
//        candidates[row][col] = MutableList(9) { (it + 1).toString() }
//        for (numb in SudokuUtil.getRelevantValues(grid, row, col)) {
//            if (numb != null) {
//                candidates[row][col].remove(numb)
//            }
//        }
//    }

    fun singleCandidatePosition(): Int {
        var difficultyScore = 0

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] != null) {
                    continue
                }

                if (candidates[row][col] == 0) {
                    throw NoSolutionException("Empty field has no possible candidates")
                }
                if ((candidates[row][col] and (candidates[row][col] - 1)) == 0) { // check if number is power of 2 (only one bit set): n & (n-1) == 0
                    addNumber(candidates[row][col], row, col)
                    difficultyScore += 100
                } else {
                    val tempCands = candidates[row][col]
                    candidates[row][col] = 0

                    val rowCands = SudokuUtil.getRow(candidates, row).reduce {
                            acc, cand -> acc or cand }
                    val colCands = SudokuUtil.getColumn(candidates, col).reduce {
                            acc, cand -> acc or cand }
                    val squareCands = SudokuUtil.getSquare(candidates, row, col).reduce {
                            acc, cand -> acc or cand }

                    candidates[row][col] = tempCands

                    for (cands in arrayOf(rowCands, colCands, squareCands)) {
                        val uniqueCands = (candidates[row][col] xor cands) and candidates[row][col]
                        if (uniqueCands != 0 && (uniqueCands and (uniqueCands - 1)) == 0) {
                            addNumber(uniqueCands, row, col)
                            difficultyScore += 100
                            break
                        } else if (uniqueCands != 0) {
                            throw NoSolutionException("Two numbers need to be in the same field")
                        }
                    }
                }
            }
        }

        return difficultyScore
    }

    fun candidateLines(): Int {
        var difficultyScore = 0

        for (i in 0..2) {
            for (j in 0..2) {
                val square = SudokuUtil.getSquareAsMat(candidates, i * 3, j * 3)
                val rowCands = Array(3) { square[it].reduce { acc, num -> acc or num } }
                val colCands = Array(3) { col ->
                    Array(3) { row ->
                        square[row][col]
                    }.reduce { acc, num -> acc or num }
                }

                for (k in rowCands.indices) {
                    val tempCands = rowCands[k]
                    rowCands[k] = 0
                    val otherRowsCands = rowCands.reduce { acc, num -> acc or num}
                    rowCands[k] = tempCands

                    val uniqueCands = (rowCands[k] xor otherRowsCands) and rowCands[k]
                    if (uniqueCands > 0) {
                        var eliminatedCands = false

                        for (l in 0..2) {
                            candidates[(i * 3) + k][(j * 3) + l] = 0
                        }
                        SudokuUtil.applyToRow(candidates, (i * 3) + k) {
                            eliminatedCands = eliminatedCands || it and uniqueCands > 0
                            it - (it and uniqueCands)
                        }
                        for (l in 0..2) {
                            candidates[(i * 3) + k][(j * 3) + l] = square[k][l]
                        }

                        if (eliminatedCands) {
                            return 200
                        }
                    }
                }

                for (k in colCands.indices) {
                    val tempCands = colCands[k]
                    colCands[k] = 0
                    val otherColsCands = colCands.reduce { acc, num -> acc or num}
                    colCands[k] = tempCands

                    val uniqueCands = (colCands[k] xor otherColsCands) and colCands[k]
                    if (uniqueCands > 0) {
                        var eliminatedCands = false

                        for (l in 0..2) {
                            candidates[(i * 3) + l][(j * 3) + k] = 0
                        }
                        SudokuUtil.applyToColumn(candidates, (j * 3) + k) {
                            eliminatedCands = eliminatedCands || it and uniqueCands > 0
                            it - (it and uniqueCands)
                        }
                        for (l in 0..2) {
                            candidates[(i * 3) + l][(j * 3) + k] = square[l][k]
                        }

                        if (eliminatedCands) {
                            return 200
                        }
                    }
                }
            }
        }

        return difficultyScore
    }
}