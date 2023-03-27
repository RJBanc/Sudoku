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
                if (BitUtil.oneBitSet(candidates[row][col])) {
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
                    if (!BitUtil.oneBitSet(matchingCands))
                        throw NoSolutionException("Single Position for multiple numbers")

                    when (arrIndex) {
                        0 -> addNumber(matchingCands, i, candsIndex)
                        1 -> addNumber(matchingCands, candsIndex, i)
                        2 -> {
                            val coords = SudokuUtil.coordsSquareConversion(i, candsIndex)
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
                                val coordsNum = SudokuUtil.coordsSquareConversion(i, numIndex)
                                val coordsOther = SudokuUtil.coordsSquareConversion(i, otherIndex)
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
                                    val coordNum = SudokuUtil.coordsSquareConversion(i, tripleCells)
                                    val newCands = BitUtil.removeBits(
                                        candidates[coordNum.first][coordNum.second],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                        (candidates[coordNum.first][coordNum.second] != newCands)

                                    candidates[coordNum.first][coordNum.second] = newCands
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

    fun singleChains(): Int {
        for (num in Array(9) { 1 shl it }) {
            val rowCells = Array(9) {
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getRow(candidates, it).withIndex()) {
                    if (cell and num > 0)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }
            val colCells = Array(9) {
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getColumn(candidates, it).withIndex()) {
                    if (cell and num > 0)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }
            val squareCells = Array(9) {
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getSquare(candidates, it).withIndex()) {
                    if (cell and num > 0)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }
            var color = Array(9) { Array<Boolean?>(9) { null } }

            for (row in 0..8) {
                for (col in 0..8) {
                    if (candidates[row][col] and num == 0 || color[row][col] != null) continue
                    color = Array(9) { Array(9) { null } }
                    color[row][col] = true
                    val queue = ArrayDeque<Pair<Int, Int>>()
                    queue.addLast(Pair(row, col))

                    while (!queue.isEmpty()) {
                        val cell = queue.removeFirst()
                        if (BitUtil.countBits(rowCells[cell.first]) == 2) {
                            val other = kotlin.math.log2(
                                (rowCells[cell.first] xor (1 shl cell.second)).toDouble()).toInt()
                            if (color[cell.first][other] == null) {
                                color[cell.first][other] = !color[cell.first][cell.second]!!
                                queue.addLast(Pair(cell.first, other))
                            }
                        }
                        if (BitUtil.countBits(colCells[cell.second]) == 2) {
                            val other = kotlin.math.log2(
                                (colCells[cell.second] xor (1 shl cell.first)).toDouble()).toInt()
                            if (color[other][cell.second] == null) {
                                color[other][cell.second] = !color[cell.first][cell.second]!!
                                queue.addLast(Pair(other, cell.second))
                            }
                        }
                        val squareIndex = SudokuUtil.coordsSquareConversion(cell.first, cell.second)
                        if (BitUtil.countBits(squareCells[squareIndex.first]) == 2) {
                            val other = SudokuUtil.coordsSquareConversion(squareIndex.first,
                                kotlin.math.log2(
                                    (squareCells[squareIndex.first] xor (1 shl squareIndex.second))
                                    .toDouble()).toInt())
                            if (color[other.first][other.second] == null) {
                                color[other.first][other.second] = !color[cell.first][cell.second]!!
                                queue.addLast(Pair(other.first, other.second))
                            }
                        }
                    }

                    for (i in 0..8) {
                        for (arr in arrayOf(
                            SudokuUtil.getRow(color, i),
                            SudokuUtil.getColumn(color, i),
                            SudokuUtil.getSquare(color, i)
                        )) {
                            if (arr.count { it == true } == 2) {
                                for (cRow in 0..8) {
                                    for (cCol in 0..8) {
                                        if (color[cRow][cCol] == false)
                                            addNumber(num, cRow, cCol)
                                    }
                                }

                                return 2100
                            }
                            else if (arr.count { it == false } == 2) {
                                for (cRow in 0..8) {
                                    for (cCol in 0..8) {
                                        if (color[cRow][cCol] == true)
                                            addNumber(num, cRow, cCol)
                                    }
                                }

                                return 2100
                            }
                        }
                    }

                    var eliminatedCands = false
                    for (cRow in 0..8) {
                        for (cCol in 0..8) {
                            if (candidates[cRow][cCol] and num == 0) continue
                            val rev = SudokuUtil.getRelevantValues(color, cRow, cCol)
                            if (rev.any { it == true } && rev.any { it == false }) {
                                candidates[cRow][cCol] = BitUtil.removeBits(
                                    candidates[cRow][cCol], num)

                                eliminatedCands = true
                            }
                        }
                    }
                    if (eliminatedCands)
                        return 2100
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
    }

    fun hiddenQuad(): Int {
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

                val potentialQuads: MutableList<Triple<Int, Int, Int>> = mutableListOf()
                for ((num, cells) in cellsPerNum.withIndex()) { //num ist die zahl - 1
                    if (BitUtil.countBits(cells) > 4) continue
                    for (quad in potentialQuads) {
                        if (BitUtil.countBits(quad.first or cells) > 4) continue

                        val extendTriple = Triple(
                            quad.first or cells,
                            quad.second +  1,
                            quad.third or (1 shl num)
                        )
                        if (extendTriple.second < 4) {
                            potentialQuads.add(0, extendTriple)
                            continue
                        }

                        var eliminatedCands = false

                        for (quadCells in BitUtil.listBitsSet(extendTriple.first)) {
                            when (arrIndex) {
                                0 -> {
                                    val newCands = BitUtil.removeBits(
                                        candidates[i][quadCells],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                            (candidates[i][quadCells] != newCands)

                                    candidates[i][quadCells] = newCands
                                }
                                1 -> {
                                    val newCands = BitUtil.removeBits(
                                        candidates[quadCells][i],
                                        extendTriple.third.inv()
                                    )
                                    eliminatedCands = eliminatedCands ||
                                            (candidates[quadCells][i] != newCands)

                                    candidates[quadCells][i] = newCands
                                }
                                2 -> {
                                    val coordsNum = SudokuUtil.coordsSquareConversion(i, quadCells)
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
                            return 5000
                    }
                    val newPotential = Triple(cells, 1, 1 shl num)
                    potentialQuads.add(newPotential)
                }
            }
        }

        return 0
    }

    fun swordfish(): Int {
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
            val potentialFish = mutableListOf<Pair<Int, Int>>()
            for (i in 0..8) {
                if (BitUtil.countBits(numRow[i]) > 3) continue
                for (pair in potentialFish) {
                    if (BitUtil.countBits(pair.first or numRow[i]) > 3) continue

                    val extendPair = Pair(
                        pair.first or numRow[i],
                        pair.second + 1
                    )
                    if (extendPair.second == 2) {
                        potentialFish.add(0, extendPair)
                        continue
                    }

                    var eliminatedCands = false
                    var index = -1
                    for (col in BitUtil.listBitsSet(extendPair.first)) {
                        SudokuUtil.applyToColumn(candidates, col) {
                            index++
                            if (numRow[index] and extendPair.first == numRow[index]) {
                                it
                            }
                            else {
                                eliminatedCands = eliminatedCands || (it ushr num) and 1 == 1
                                BitUtil.removeBits(it, 1 shl num)
                            }
                        }
                    }

                    if (eliminatedCands)
                        return 6000
                }
                potentialFish.add(Pair(numRow[i], 1))
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
            val potentialFish = mutableListOf<Pair<Int, Int>>()
            for (i in 0..8) {
                if (BitUtil.countBits(numCol[i]) > 3) continue
                for (pair in potentialFish) {
                    if (BitUtil.countBits(numCol[i] or pair.first) > 3) continue

                    val extendPair = Pair(
                        pair.first or numCol[i],
                        pair.second + 1
                    )
                    if (extendPair.second == 2) {
                        potentialFish.add(0, extendPair)
                        continue
                    }

                    var eliminatedCands = false
                    var index = -1
                    for (row in BitUtil.listBitsSet(extendPair.first)) {
                        SudokuUtil.applyToRow(candidates, row) {
                            index++
                            if (numCol[index] and extendPair.first == numCol[index]) {
                                it
                            }
                            else {
                                eliminatedCands = eliminatedCands || (it ushr num) and 1 == 1
                                BitUtil.removeBits(it, 1 shl num)
                            }
                        }
                    }

                    if (eliminatedCands)
                        return 6000
                }
                potentialFish.add(Pair(numCol[i], 1))
            }
        }

        return 0
    }
}