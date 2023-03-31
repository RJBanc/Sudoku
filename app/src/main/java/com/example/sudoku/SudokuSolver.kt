package com.example.sudoku

class SudokuSolver(private var grid: Array<Array<String?>>) {
    internal val candidates = Array(9) { Array(9) { 0b111111111 } }
    private var emptyCells = 81

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

    private val techniqueCosts = mapOf(
        "singleCandPos" to Pair(100, 100),
        "candLine" to Pair(350, 200),
        "boxLine" to Pair(600, 300),
        "nakedPair" to Pair(750, 500),
        "hiddenPair" to Pair(1500, 1200),
        "nakedTrip" to Pair(2000, 1400),
        "hiddenTrip" to Pair(2400, 1600),
        "xWing" to Pair(2800, 1600),
        "yWing" to Pair(3600, 2000),
        "singChain" to Pair(4200, 2100),
        "nakedQuad" to Pair(5000, 4000),
        "hiddenQuad" to Pair(7000, 5000),
        "swordfish" to Pair(8000, 6000),
        "bug" to Pair(8000, 6000)
    )

    private val techniqueUses = buildMap {
        for (key in techniqueCosts.keys)
            put(key, 0)
    }.toMutableMap()


    init {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == null) {
                    continue
                }

                emptyCells--
                candidates[row][col] = 0
                SudokuUtil.applyToRelevantValues(candidates, row, col) {
                    BitUtil.removeBits(it, stringToBitMap.getValue(grid[row][col]))
                }
            }
        }
    }

    fun solve(): Boolean {
        val techniques = listOf(
            ::singleCandidatePosition,
            ::candidateLines,
            ::boxLineReduction,
            ::nakedPair,
            ::hiddenPair,
            ::nakedTriple,
            ::hiddenTriple,
            ::xWing,
            ::yWing,
            ::singleChains,
            ::nakedQuad,
            ::hiddenQuad,
            ::swordfish,
            ::BUG
        )
        try {
            var index = 0
            while (index < techniques.size) {
                if (finished())
                    return true
                if (techniques[index]())
                    index = 0
                else
                    index++
            }
            throw NoSolutionException("No technique applicable")

        } catch (e: NoSolutionException) {
            return false
        }
    }

    fun addNumber(numb: String, row: Int, col: Int) {
        grid[row][col] = numb
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            BitUtil.removeBits(it, stringToBitMap.getValue(numb))
        }
        emptyCells--
    }

    fun addNumber(numb: Int, row: Int, col: Int) {
        grid[row][col] = bitToStringMap.getValue(numb)
        candidates[row][col] = 0
        SudokuUtil.applyToRelevantValues(candidates, row, col) {
            BitUtil.removeBits(it, numb)
        }
        emptyCells--
    }

    fun finished(): Boolean {
        return emptyCells == 0
    }

    fun difficultyScore(): Int {
        var score = 0
        for ((key, value) in techniqueUses.entries) {
            if (value == 0) continue

            score += techniqueCosts[key]!!.first + (techniqueCosts[key]!!.second * (value - 1))
        }
        return score
    }

    fun singleCandidatePosition(): Boolean {
        var uses = 0

        val rowUnique = Array(9) {
            BitUtil.uniqueBits(SudokuUtil.getRow(candidates, it))
        }
        val colUnique = Array(9) {
            BitUtil.uniqueBits(SudokuUtil.getColumn(candidates, it))
        }
        val squareUnique = Array(9) {
            BitUtil.uniqueBits(SudokuUtil.getSquare(candidates, it))
        }

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
                    uses++
                }

                val squ = SudokuUtil.coordsSquareConversion(row, col).first
                val relevantUnique = rowUnique[row] or colUnique[col] or squareUnique[squ]
                if (BitUtil.oneBitSet(relevantUnique and candidates[row][col])) {
                    addNumber(relevantUnique and candidates[row][col], row, col)
                    uses++
                } else if (relevantUnique and candidates[row][col] > 0) {
                    throw NoSolutionException("Single Position for multiple numbers")
                }
            }
        }

        techniqueUses["singleCandPos"] = (techniqueUses["singleCandPos"] ?: 0) + uses
        return uses > 0
    }

    fun candidateLines(): Boolean {
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

                    if (eliminatedCands) {
                        techniqueUses["candLine"] = (techniqueUses["candLine"] ?: 0) + 1
                        return true
                    }
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

                    if (eliminatedCands) {
                        techniqueUses["candLine"] = (techniqueUses["candLine"] ?: 0) + 1
                        return true
                    }
                }
            }
        }

        return false
    }

    fun boxLineReduction(): Boolean {
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

                    if (eliminatedCands) {
                        techniqueUses["boxLine"] = (techniqueUses["boxLine"] ?: 0) + 1
                        return true
                    }
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

                    if (eliminatedCands) {
                        techniqueUses["boxLine"] = (techniqueUses["boxLine"] ?: 0) + 1
                        return true
                    }
                }
            }
        }

        return false
    }

    fun nakedPair(): Boolean {
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

                        if (eliminatedCands) {
                            techniqueUses["nakedPair"] = (techniqueUses["nakedPair"] ?: 0) + 1
                            return true
                        }
                    }

                    potentialPairs.add(num)
                }
            }
        }

        return false
    }

    fun hiddenPair(): Boolean {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                for (numIndex in 0 until (arr.size - 1)) {
                    val temp = arr[numIndex]
                    arr[numIndex] = 0
                    val uniqueCands = BitUtil.uniqueBits(arr)
                    arr[numIndex] = temp

                    if (BitUtil.countBits(uniqueCands) != 2) continue
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

                        techniqueUses["hiddenPair"] = (techniqueUses["hiddenPair"] ?: 0) + 1
                        return true
                    }
                }
            }
        }

        return false
    }

    fun nakedTriple(): Boolean {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val potentialTriplets: MutableList<Pair<Int, Int>> = mutableListOf()
                for (num in arr) {
                    if (num == 0) continue
                    if (BitUtil.countBits(num) > 3) continue
                    val iter = potentialTriplets.listIterator()
                    while (iter.hasNext()) {
                        val pair = iter.next()
                        if (BitUtil.countBits(pair.first or num) > 3) continue
                        if (pair.second == 1) {
                            val extendPair = Pair(pair.first or num, 2)
                            iter.add(extendPair)
                            continue
                        }

                        var eliminatedCands = false
                        val transform: (Int) -> Int = {
                            if (it and (pair.first or num) == it)
                                it
                            else {
                                eliminatedCands = eliminatedCands || it and (pair.first or num) > 0
                                BitUtil.removeBits(it, pair.first or num)
                            }
                        }
                        when (arrIndex) {
                            0 -> SudokuUtil.applyToRow(candidates, i, transform)
                            1 -> SudokuUtil.applyToColumn(candidates, i, transform)
                            2 -> SudokuUtil.applyToSquare(candidates, i, transform)
                        }
                        if (eliminatedCands) {
                            techniqueUses["nakedTrip"] = (techniqueUses["nakedTrip"] ?: 0) + 1
                            return true
                        }
                    }
                    val newPotential = Pair(num, 1)
                    potentialTriplets.add(newPotential)
                }
            }
        }

        return false
    }

    fun hiddenTriple(): Boolean {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val cellsPerNum = Array(9) {0}
                for (j in arr.indices) {
                    for (k in cellsPerNum.indices) {
                        if ((arr[j] ushr k) and 1 == 1)
                            cellsPerNum[k] = cellsPerNum[k] or (1 shl j)
                    }
                }

                val potentialTriplets: MutableList<Triple<Int, Int, Int>> = mutableListOf()
                for ((num, cells) in cellsPerNum.withIndex()) { //num ist die zahl - 1
                    if (cells == 0) continue
                    if (BitUtil.countBits(cells) > 3) continue
                    val iter = potentialTriplets.listIterator()
                    while (iter.hasNext()) {
                        val triple = iter.next()
                        if (BitUtil.countBits(triple.first or cells) > 3) continue

                        val extendTriple = Triple(
                            triple.first or cells,
                            triple.second +  1,
                            triple.third or (1 shl num)
                        )
                        if (extendTriple.second == 2) {
                            iter.add(extendTriple)
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

                        if (eliminatedCands) {
                            techniqueUses["hiddenTrip"] = (techniqueUses["hiddenTrip"] ?: 0) + 1
                            return true
                        }
                    }
                    val newPotential = Triple(cells, 1, 1 shl num)
                    potentialTriplets.add(newPotential)
                }
            }
        }

        return false
    }

    fun xWing(): Boolean {
        for (num in 0..8) {
            val numRow = Array(9) {
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getRow(candidates, it).withIndex()) {
                    if ((cell ushr num) and 1 == 1)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }

            for (i in 0..7) {
                if (BitUtil.countBits(numRow[i]) != 2) continue
                for (j in (i + 1)..8) {
                    if (numRow[i] != numRow[j]) continue

                    var eliminatedCands = false
                    for (col in BitUtil.listBitsSet(numRow[i])) {
                        var index = -1
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

                    if (eliminatedCands) {
                        techniqueUses["xWing"] = (techniqueUses["xWing"] ?: 0) + 1
                        return true
                    }
                }
            }
        }


        for (num in 0..8) {
            val numCol = Array(9) {
                var cells = 0
                for ((cellIndex, cell) in SudokuUtil.getColumn(candidates, it).withIndex()) {
                    if ((cell ushr num) and 1 == 1)
                        cells = cells or (1 shl cellIndex)
                }
                cells
            }

            for (i in 0..7) {
                if (BitUtil.countBits(numCol[i]) != 2) continue
                for (j in (i + 1)..8) {
                    if (numCol[i] != numCol[j]) continue

                    var eliminatedCands = false
                    for (row in BitUtil.listBitsSet(numCol[i])) {
                        var index = -1
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

                    if (eliminatedCands) {
                        techniqueUses["xWing"] = (techniqueUses["xWing"] ?: 0) + 1
                        return true
                    }
                }
            }
        }

        return false
    }

    fun yWing(): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                val ab = candidates[row][col]
                if (BitUtil.countBits(ab) != 2) continue

                val corners = mutableListOf<Pair<Int, Int>>()
                for (coord in SudokuUtil.getRelevantCoords(row, col)) {
                    val coordVal = candidates[coord.first][coord.second]
                    if (BitUtil.countBits(coordVal) != 2) continue
                    if (coordVal == ab || coordVal and ab == 0)
                        continue


                    for (cornerCoord in corners) {
                        val corner = candidates[cornerCoord.first][cornerCoord.second]
                        if (corner and coordVal == 0 || corner == coordVal) continue
                        if (corner and coordVal and ab != 0) continue

                        val bcRev = SudokuUtil.getRelevantCoords(coord.first, coord.second)
                        val acRev = SudokuUtil.getRelevantCoords(
                            cornerCoord.first, cornerCoord.second)

                        if (cornerCoord in bcRev) continue

                        val c = corner and coordVal
                        var eliminatedCands = false

                        for (potCand in bcRev.intersect(acRev.toSet())) {
                            if (candidates[potCand.first][potCand.second] and c == 0) continue

                            eliminatedCands = true
                            candidates[potCand.first][potCand.second] = BitUtil.removeBits(
                                candidates[potCand.first][potCand.second], c
                            )
                        }

                        if (eliminatedCands) {
                            techniqueUses["yWing"] = (techniqueUses["yWing"] ?: 0) + 1
                            return true
                        }
                    }
                    corners.add(coord)
                }
            }
        }

        return false
    }

    fun singleChains(): Boolean {
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

                                techniqueUses["singChain"] = (techniqueUses["singChain"] ?: 0) + 1
                                return true
                            }
                            else if (arr.count { it == false } == 2) {
                                for (cRow in 0..8) {
                                    for (cCol in 0..8) {
                                        if (color[cRow][cCol] == true)
                                            addNumber(num, cRow, cCol)
                                    }
                                }

                                techniqueUses["singChain"] = (techniqueUses["singChain"] ?: 0) + 1
                                return true
                            }
                        }
                    }

                    var eliminatedCands = false
                    for (cRow in 0..8) {
                        for (cCol in 0..8) {
                            if (candidates[cRow][cCol] and num == 0) continue
                            val temp = color[cRow][cCol]
                            color[cRow][cCol] = null
                            val rev = SudokuUtil.getRelevantValues(color, cRow, cCol)
                            color[cRow][cCol] = temp
                            if (rev.any { it == true } && rev.any { it == false }) {
                                candidates[cRow][cCol] = BitUtil.removeBits(
                                    candidates[cRow][cCol], num)

                                eliminatedCands = true
                            }
                        }
                    }
                    if (eliminatedCands) {
                        techniqueUses["singChain"] = (techniqueUses["singChain"] ?: 0) + 1
                        return true
                    }
                }
            }
        }

        return false
    }

    fun nakedQuad(): Boolean {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val potentialQuads: MutableList<Pair<Int, Int>> = mutableListOf()
                for (num in arr) {
                    if (num == 0) continue
                    if (BitUtil.countBits(num) > 4) continue
                    val iter = potentialQuads.listIterator()
                    while (iter.hasNext()) {
                        val pair = iter.next()
                        if (BitUtil.countBits(pair.first or num) > 4) continue
                        if (pair.second < 3) {
                            val extendPair = Pair(pair.first or num, pair.second + 1)
                            iter.add(extendPair)
                            continue
                        }

                        var eliminatedCands = false
                        val transform: (Int) -> Int = {
                            if (it and (pair.first or num) == it)
                                it
                            else {
                                eliminatedCands = eliminatedCands || it and (pair.first or num) > 0
                                BitUtil.removeBits(it, pair.first or num)
                            }
                        }
                        when (arrIndex) {
                            0 -> SudokuUtil.applyToRow(candidates, i, transform)
                            1 -> SudokuUtil.applyToColumn(candidates, i, transform)
                            2 -> SudokuUtil.applyToSquare(candidates, i, transform)
                        }
                        if (eliminatedCands) {
                            techniqueUses["nakedQuad"] = (techniqueUses["nakedQuad"] ?: 0) + 1
                            return true
                        }
                    }
                    val newPotential = Pair(num, 1)
                    potentialQuads.add(newPotential)
                }
            }
        }

        return false
    }

    fun hiddenQuad(): Boolean {
        for (i in 0..8) {
            for ((arrIndex, arr) in arrayOf(
                SudokuUtil.getRow(candidates, i),
                SudokuUtil.getColumn(candidates, i),
                SudokuUtil.getSquare(candidates, i)).withIndex()
            ) {
                val cellsPerNum = Array(9) {0}
                for (j in arr.indices) {
                    for (k in cellsPerNum.indices) {
                        if ((arr[j] ushr k) and 1 == 1)
                            cellsPerNum[k] = cellsPerNum[k] or (1 shl j)
                    }
                }

                val potentialQuads: MutableList<Triple<Int, Int, Int>> = mutableListOf()
                for ((num, cells) in cellsPerNum.withIndex()) { //num ist die zahl - 1
                    if (cells == 0) continue
                    if (BitUtil.countBits(cells) > 4) continue
                    val iter = potentialQuads.listIterator()
                    while (iter.hasNext()) {
                        val quad = iter.next()
                        if (BitUtil.countBits(quad.first or cells) > 4) continue

                        val extendTriple = Triple(
                            quad.first or cells,
                            quad.second +  1,
                            quad.third or (1 shl num)
                        )
                        if (extendTriple.second < 4) {
                            iter.add(extendTriple)
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

                        if (eliminatedCands) {
                            techniqueUses["hiddenQuad"] = (techniqueUses["hiddenQuad"] ?: 0) + 1
                            return true
                        }
                    }
                    val newPotential = Triple(cells, 1, 1 shl num)
                    potentialQuads.add(newPotential)
                }
            }
        }

        return false
    }

    fun swordfish(): Boolean {
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
                if (numRow[i] == 0) continue
                if (BitUtil.countBits(numRow[i]) > 3) continue
                val iter = potentialFish.listIterator()
                while (iter.hasNext()) {
                    val pair = iter.next()
                    if (BitUtil.countBits(pair.first or numRow[i]) > 3) continue

                    val extendPair = Pair(
                        pair.first or numRow[i],
                        pair.second + 1
                    )
                    if (extendPair.second == 2) {
                        iter.add(extendPair)
                        continue
                    }

                    var eliminatedCands = false
                    for (col in BitUtil.listBitsSet(extendPair.first)) {
                        var index = -1
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

                    if (eliminatedCands) {
                        techniqueUses["swordfish"] = (techniqueUses["swordfish"] ?: 0) + 1
                        return true
                    }
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
                if (numCol[i] == 0) continue
                if (BitUtil.countBits(numCol[i]) > 3) continue
                val iter = potentialFish.listIterator()
                while (iter.hasNext()) {
                    val pair = iter.next()
                    if (BitUtil.countBits(numCol[i] or pair.first) > 3) continue

                    val extendPair = Pair(
                        pair.first or numCol[i],
                        pair.second + 1
                    )
                    if (extendPair.second == 2) {
                        iter.add(extendPair)
                        continue
                    }

                    var eliminatedCands = false
                    for (row in BitUtil.listBitsSet(extendPair.first)) {
                        var index = -1
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

                    if (eliminatedCands) {
                        techniqueUses["swordfish"] = (techniqueUses["swordfish"] ?: 0) + 1
                        return true
                    }
                }
                potentialFish.add(Pair(numCol[i], 1))
            }
        }

        return false
    }

    fun BUG(): Boolean {
        var triPose: Pair<Int, Int>? = null
        for (row in 0..8) {
            for (col in 0..8) {
                if (BitUtil.countBits(candidates[row][col]) > 3) return false
                if (BitUtil.countBits(candidates[row][col]) == 3) {
                    if (triPose != null)
                        return false
                    triPose = Pair(row, col)
                }
            }
        }
        if (triPose == null) return false

        val tmp = candidates[triPose.first][triPose.second]
        candidates[triPose.first][triPose.second] = 0
        val rowBits = SudokuUtil.getRow(candidates, triPose.first)
        val solution = tmp xor BitUtil.uniqueBits(rowBits)
        addNumber(solution, triPose.first, triPose.second)

        techniqueUses["bug"] = (techniqueUses["bug"] ?: 0) + 1
        return true
    }
}