package com.example.sudoku

class SudokuSolver {
    private val candidates = Array(9) { Array(9) { 0b111111111 } }
    private var grid: Array<Array<String?>>

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
                    BitUtil.removeBits(it, stringToBitMap.getValue(grid[row][col]))
                }
            }
        }
    }

    fun addNumber(numb: String, row: Int, col: Int) {
        grid[row][col] = numb
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            BitUtil.removeBits(it, stringToBitMap.getValue(numb))
        }
    }

    fun addNumber(numb: Int, row: Int, col: Int) {
        grid[row][col] = bitToStringMap.getValue(numb)
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            BitUtil.removeBits(it, numb)
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

    fun singleCandidate(): Int {
        var difficultyScore = 0

        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] != null) {
                    continue
                }

                if (candidates[row][col] == 0) {
                    throw NoSolutionException("Empty field has no possible candidates")
                }
                if (BitUtil.isPowerOfTwo(candidates[row][col])) {
                    addNumber(candidates[row][col], row, col)
                    difficultyScore += 100
                }
            }
        }

        return difficultyScore
    }

    fun singlePosition(): Int {
        var difficultyScore = 0

        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                var uniqueCands = BitUtil.uniqueBits(arr)

                if (uniqueCands == 0) continue

                for ((candsIndex, cands) in arr.withIndex()) {
                    val matchingCands = cands and uniqueCands
                    if (matchingCands == 0) continue
                    if (matchingCands and (matchingCands - 1) != 0)
                        throw NoSolutionException("Single Position for multiple numbers")

                    when (arrIndex) {
                        0 -> addNumber(matchingCands, i, candsIndex)
                        1 -> addNumber(matchingCands, candsIndex, i)
                        2 -> {
                            val coords = SudokuUtil.getCoordsInSqaure(i, candsIndex)
                            addNumber(matchingCands, coords.first, coords.second)
                        }
                    }
                    difficultyScore += 100

                    uniqueCands -= cands
                    if (uniqueCands == 0) break

                }
            }
        }

        return difficultyScore
    }

    fun candidateLines(): Int {
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

                    val uniqueCands = BitUtil.uniqueBits(rowCands[k], otherRowsCands)
                    if (uniqueCands == 0) continue

                    for (l in 0..2) {
                        candidates[(i * 3) + k][(j * 3) + l] = 0
                    }
                    var eliminatedCands = false
                    SudokuUtil.applyToRow(candidates, (i * 3) + k) {
                        eliminatedCands = eliminatedCands || it and uniqueCands > 0
                        BitUtil.removeBits(it, uniqueCands)
                    }
                    for (l in 0..2) {
                        candidates[(i * 3) + k][(j * 3) + l] = square[k][l]
                    }

                    if (eliminatedCands)
                        return 200
                }

                for (k in colCands.indices) {
                    val tempCands = colCands[k]
                    colCands[k] = 0
                    val otherColsCands = colCands.reduce { acc, num -> acc or num}
                    colCands[k] = tempCands

                    val uniqueCands = BitUtil.uniqueBits(colCands[k], otherColsCands)
                    if (uniqueCands == 0) continue

                    for (l in 0..2) {
                        candidates[(i * 3) + l][(j * 3) + k] = 0
                    }
                    var eliminatedCands = false
                    SudokuUtil.applyToColumn(candidates, (j * 3) + k) {
                        eliminatedCands = eliminatedCands || it and uniqueCands > 0
                        BitUtil.removeBits(it, uniqueCands)
                    }
                    for (l in 0..2) {
                        candidates[(i * 3) + l][(j * 3) + k] = square[l][k]
                    }

                    if (eliminatedCands)
                        return 200
                }
            }
        }

        return 0
    }

    fun boxLineReduction(): Int {
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
                    for (l in square.indices) {
                        candidates[(i * 3) + k][(j * 3) + l] = 0
                    }
                    val outsideCands = SudokuUtil.getRow(candidates, (i * 3) + k).reduce {
                        acc, num -> acc or num
                    }

                    val uniqueCands = BitUtil.uniqueBits(rowCands[k], outsideCands)
                    var eliminatedCands = false
                    if (uniqueCands > 0) {
                        SudokuUtil.applyToSquare(candidates, i * 3, j * 3) {
                            eliminatedCands = eliminatedCands || it and uniqueCands > 0
                            BitUtil.removeBits(it, uniqueCands)
                        }
                    }

                    for (l in square.indices) {
                        candidates[(i * 3) + k][(j * 3) + l] = square[k][l]
                    }

                    if (eliminatedCands)
                        return 350
                }

                for (k in colCands.indices) {
                    for (l in square.indices) {
                        candidates[(i * 3) + l][(j * 3) + k] = 0
                    }
                    val outsideCands = SudokuUtil.getColumn(candidates, (j * 3) + k).reduce {
                            acc, num -> acc or num
                    }

                    val uniqueCands = BitUtil.uniqueBits(colCands[k], outsideCands)
                    var eliminatedCands = false
                    if (uniqueCands > 0) {
                        SudokuUtil.applyToSquare(candidates, i * 3, j * 3) {
                            eliminatedCands = eliminatedCands || it and uniqueCands > 0
                            BitUtil.removeBits(it, uniqueCands)
                        }
                    }

                    for (l in square.indices) {
                        candidates[(i * 3) + l][(j * 3) + k] = square[l][k]
                    }

                    if (eliminatedCands)
                        return 350
                }
            }
        }

        return 0
    }

    fun nakedPair(): Int {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val potentialPairs = mutableListOf<Int>()
                for (num in arr) {
                    if (BitUtil.countBits(num) != 2) continue

                    if (num in potentialPairs) {
                        var eliminatedCands = false
                        val transform: (Int) -> Int = {
                            if (it != num && it and num > 0) {
                                eliminatedCands = true
                                BitUtil.removeBits(it, num)
                            }
                            else
                                it
                        }

                        when (arrIndex) {
                            0 -> SudokuUtil.applyToRow(candidates, i, transform)
                            1 -> SudokuUtil.applyToColumn(candidates, i, transform)
                            2 -> SudokuUtil.applyToSquare(candidates, i, transform)
                        }

                        if (eliminatedCands)
                            return 500
                    }

                    potentialPairs.add(num)
                }
            }
        }

        return 0
    }

    fun hiddenPair(): Int {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                for (numIndex in 0 until (arr.size - 1)) {
                    val rest = arr.slice((numIndex + 1) until arr.size).toTypedArray()
                    val uniqueCands = BitUtil.uniqueBits(rest)

                    if (uniqueCands != 2) continue
                    if (uniqueCands and arr[numIndex] != uniqueCands) continue

                    for (otherIndex in (numIndex + 1) until arr.size) {
                        if (arr[otherIndex] and uniqueCands != uniqueCands) continue
                        if (BitUtil.countBits(arr[otherIndex]) < 3 &&
                            BitUtil.countBits(arr[numIndex]) < 3) break

                        when (arrIndex) {
                            0 -> {
                                candidates[i][numIndex] = uniqueCands
                                candidates[i][otherIndex] = uniqueCands
                            }
                            1 -> {
                                candidates[numIndex][i] = uniqueCands
                                candidates[otherIndex][i] = uniqueCands
                            }
                            2 -> {
                                val coordsNum = SudokuUtil.getCoordsInSqaure(i, numIndex)
                                val coordsOther = SudokuUtil.getCoordsInSqaure(i, otherIndex)
                                candidates[coordsNum.first][coordsNum.second] = uniqueCands
                                candidates[coordsOther.first][coordsOther.second] = uniqueCands
                            }
                        }

                        return 1200
                    }
                }
            }
        }

        return 0
    }

    fun nakedTriple(): Int {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val potentialTriplets: MutableList<Pair<Int, Int>> = mutableListOf()
                for (num in arr) {
                    if (BitUtil.countBits(num) > 3) continue
                    for (pair in potentialTriplets) {
                        if (BitUtil.countBits(pair.first or num) > 3) continue
                        if (pair.second == 1) {
                            val extendPair = Pair(pair.first or num, 2)
                            potentialTriplets.add(0, extendPair)
                            continue
                        }

                        var eliminatedCands = false
                        val transform: (Int) -> Int = {
                            if (BitUtil.countBits(it or pair.first or num) < 4)
                                it
                            else {
                                eliminatedCands = true
                                BitUtil.removeBits(it, pair.first or num)
                            }
                        }
                        when (arrIndex) {
                            0 -> SudokuUtil.applyToRow(candidates, i, transform)
                            1 -> SudokuUtil.applyToColumn(candidates, i, transform)
                            2 -> SudokuUtil.applyToSquare(candidates, i, transform)
                        }
                        if (eliminatedCands)
                            return 1400
                    }
                    val newPotential = Pair(num, 1)
                    potentialTriplets.add(newPotential)
                }
            }
        }

        return 0
    }

    fun hiddenTriple(): Int {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val cellsPerNum = Array(9) {0}
                for (i in arr.indices) {
                    for (j in cellsPerNum.indices) {
                        if ((arr[i] ushr j) and 1 == 1)
                            cellsPerNum[j] = cellsPerNum[j] or (1 shl i)
                    }
                }

                val potentialTriplets: MutableList<Triple<Int, Int, Int>> = mutableListOf()
                for ((num, cells) in cellsPerNum.withIndex()) { //num ist die zahl - 1
                    if (BitUtil.countBits(cells) > 3) continue
                    for (triple in potentialTriplets) {
                        if (BitUtil.countBits(triple.first or cells) > 3) continue

                        val extendTriple = Triple(
                            triple.first or cells,
                            triple.second +  1,
                            triple.third or (1 shl num)
                        )
                        if (extendTriple.second == 2) {
                            potentialTriplets.add(0, extendTriple)
                            continue
                        }

                        var eliminatedCands = false

                        for (tripleCells in BitUtil.listBitsSet(extendTriple.first)) {
                            when (arrIndex) {
                                0 -> {
                                    val newCands = BitUtil.removeBits(
                                        candidates[i][tripleCells],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                        (candidates[i][tripleCells] != newCands)

                                    candidates[i][tripleCells] = newCands
                                }
                                1 -> {
                                    val newCands = BitUtil.removeBits(
                                        candidates[tripleCells][i],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                        (candidates[tripleCells][i] != newCands)

                                    candidates[tripleCells][i] = newCands
                                }
                                2 -> {
                                    val coordsNum = SudokuUtil.getCoordsInSqaure(i, tripleCells)
                                    val newCands = BitUtil.removeBits(
                                        candidates[coordsNum.first][coordsNum.second],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                        (candidates[coordsNum.first][coordsNum.second] != newCands)

                                    candidates[coordsNum.first][coordsNum.second] = newCands
                                }
                            }
                        }

                        if (eliminatedCands)
                            return 1600
                    }
                    val newPotential = Triple(cells, 1, 1 shl num)
                    potentialTriplets.add(newPotential)
                }
            }
        }

        return 0
    }

    fun xWing(): Int {
        val cellsPerNumRows = Array(9) { num ->
            Array(9) { rowIndex ->
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getRow(candidates, rowIndex).withIndex()) {
                    if ((cell ushr num) and 1 == 1)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }
        }

        for ((num, numRow) in cellsPerNumRows.withIndex()) {
            for (i in 0..7) {
                if (BitUtil.countBits(numRow[i]) != 2) continue
                for (j in (i + 1)..8) {
                    if (numRow[i] != numRow[j]) continue

                    var eliminatedCands = false
                    var index = -1
                    for (col in BitUtil.listBitsSet(numRow[i])) {
                        SudokuUtil.applyToColumn(candidates, col) {
                            index++
                            if (index == i || index == j) {
                                it
                            }
                            else {
                                eliminatedCands = eliminatedCands || (it ushr num) and 1 == 1
                                BitUtil.removeBits(it, 1 shl num)
                            }
                        }
                    }

                    if (eliminatedCands)
                        return 1600
                }
            }
        }


        val cellsPerNumCols = Array(9) { num ->
            Array(9) { colIndex ->
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getColumn(candidates, colIndex).withIndex()) {
                    if ((cell ushr num) and 1 == 1)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }
        }

        for ((num, numCol) in cellsPerNumCols.withIndex()) {
            for (i in 0..7) {
                if (BitUtil.countBits(numCol[i]) != 2) continue
                for (j in (i + 1)..8) {
                    if (numCol[i] != numCol[j]) continue

                    var eliminatedCands = false
                    var index = -1
                    for (row in BitUtil.listBitsSet(numCol[i])) {
                        SudokuUtil.applyToRow(candidates, row) {
                            index++
                            if (index == i || index == j) {
                                it
                            }
                            else {
                                eliminatedCands = eliminatedCands || (it ushr num) and 1 == 1
                                BitUtil.removeBits(it, 1 shl num)
                            }
                        }
                    }

                    if (eliminatedCands)
                        return 1600
                }
            }
        }

        return 0
    }

    fun forcingChains(): Int {
        for (row in 0..8) {
            for (col in 0..8) {
                if (BitUtil.countBits(candidates[row][col]) != 2) continue

                val firstBit = (candidates[row][col] - 1) and candidates[row][col]
                val markedf = Array(9) { Array(9) { 0 } }
                markedf[row][col] = firstBit
                val path = ArrayDeque<Pair<Int, Int>>()
                
                path.addLast(Pair(row, col))
                while (!path.isEmpty()) {
                    val cell = path.removeLast()
                    for (i in 0..8) {
                        //row
                        if (markedf[cell.first][i] == 0 &&
                            candidates[cell.first][i] and markedf[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[cell.first][i]) == 2
                        ) {
                            markedf[cell.first][i] = BitUtil.removeBits(candidates[cell.first][i],
                                markedf[cell.first][cell.second])
                            path.addLast(Pair(cell.first, i))
                        }

                        //col
                        if (markedf[i][cell.second] == 0 &&
                            candidates[i][cell.second] and markedf[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[i][cell.second]) == 2
                        ) {
                            markedf[i][cell.second] = BitUtil.removeBits(candidates[i][cell.second],
                                markedf[cell.first][cell.second])
                            path.addLast(Pair(i, cell.second))
                        }

                        //square
                        val sqRow = (kotlin.math.floor(cell.first / 3.0) * 3 +
                                kotlin.math.floor(i / 3.0)).toInt()
                        val sqCol = (kotlin.math.floor(cell.second / 3.0) * 3 + i % 3).toInt()
                        if (markedf[sqRow][sqCol] == 0 &&
                            candidates[sqRow][sqCol] and markedf[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[sqRow][sqCol]) == 2
                        ) {
                            markedf[sqRow][sqCol] = BitUtil.removeBits(candidates[sqRow][sqCol],
                                markedf[cell.first][cell.second])
                            path.addLast(Pair(sqRow, sqCol))
                        }
                    }
                }
                
                val secondBit = firstBit xor candidates[row][col]
                val markeds = Array(9) { Array(9) { 0 } }
                markeds[row][col] = secondBit
                
                path.addLast(Pair(row, col))
                while (!path.isEmpty()) {
                    val cell = path.removeLast()
                    for (i in 0..8) {
                        //row
                        if (markeds[cell.first][i] == 0 &&
                            candidates[cell.first][i] and markeds[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[cell.first][i]) == 2
                        ) {
                            markeds[cell.first][i] = BitUtil.removeBits(candidates[cell.first][i],
                                markeds[cell.first][cell.second])
                            if (markedf[cell.first][i] == markeds[cell.first][i]) {
                                addNumber(markeds[cell.first][i], cell.first, i)
                                return 2100
                            }
                            path.addLast(Pair(cell.first, i))
                        }

                        //col
                        if (markeds[i][cell.second] == 0 &&
                            candidates[i][cell.second] and markeds[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[i][cell.second]) == 2
                        ) {
                            markeds[i][cell.second] = BitUtil.removeBits(candidates[i][cell.second],
                                markeds[cell.first][cell.second])
                            if (markedf[i][cell.second] == markeds[i][cell.second]) {
                                addNumber(markeds[i][cell.second], i, cell.second)
                                return 2100
                            }
                            path.addLast(Pair(i, cell.second))
                        }

                        //square
                        val sqRow = (kotlin.math.floor(cell.first / 3.0) * 3 +
                                kotlin.math.floor(i / 3.0)).toInt()
                        val sqCol = (kotlin.math.floor(cell.second / 3.0) * 3 + i % 3).toInt()
                        if (markeds[sqRow][sqCol] == 0 &&
                            candidates[sqRow][sqCol] and markeds[cell.first][cell.second] != 0 &&
                            BitUtil.countBits(candidates[sqRow][sqCol]) == 2
                        ) {
                            markeds[sqRow][sqCol] = BitUtil.removeBits(candidates[sqRow][sqCol],
                                markeds[cell.first][cell.second])
                            if (markedf[sqRow][sqCol] == markeds[sqRow][sqCol]) {
                                addNumber(markeds[sqRow][sqCol], sqRow, sqCol)
                                return 2100
                            }
                            path.addLast(Pair(sqRow, sqCol))
                        }
                    }
                }
            }
        }

        return 0
    }

    fun nakedQuad(): Int {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val potentialQuads: MutableList<Pair<Int, Int>> = mutableListOf()
                for (num in arr) {
                    if (BitUtil.countBits(num) > 4) continue
                    for (pair in potentialQuads) {
                        if (BitUtil.countBits(pair.first or num) > 4) continue
                        if (pair.second < 3) {
                            val extendPair = Pair(pair.first or num, pair.second + 1)
                            potentialQuads.add(0, extendPair)
                            continue
                        }

                        var eliminatedCands = false
                        val transform: (Int) -> Int = {
                            if (BitUtil.countBits(it or pair.first or num) < 5)
                                it
                            else {
                                eliminatedCands = true
                                BitUtil.removeBits(it, pair.first or num)
                            }
                        }
                        when (arrIndex) {
                            0 -> SudokuUtil.applyToRow(candidates, i, transform)
                            1 -> SudokuUtil.applyToColumn(candidates, i, transform)
                            2 -> SudokuUtil.applyToSquare(candidates, i, transform)
                        }
                        if (eliminatedCands)
                            return 4000
                    }
                    val newPotential = Pair(num, 1)
                    potentialQuads.add(newPotential)
                }
            }
        }

        return 0

        return 0
    }
}