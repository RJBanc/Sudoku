package com.example.sudoku

import androidx.lifecycle.MutableLiveData
import kotlin.random.Random
import kotlin.random.nextInt

class SudokuLogic {
    private var difficulty: Difficulty? = null
    private val fields: Array<Array<MutableLiveData<SudokuField>>>
    private var solution: Array<Array<String?>>
    private val symbols = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    private var lastHighlighted: Array<MutableLiveData<SudokuField>>? = null
    private var currFieldCoords: Pair<Int, Int>? = null

    constructor() {
        fields  = Array(9) {
            Array(9) {
                MutableLiveData<SudokuField>(
                    SudokuField(
                        isEnabled = true,
                        isHighlighted = false
                    )
                )
            }
        }
        solution = Array(9) { arrayOfNulls<String>(9) }
    }

    fun newGame(difficulty: Difficulty? = null) {
        this.difficulty = difficulty

        solution = Array(9) { arrayOfNulls<String>(9) }
        makeSolution()

        var attempts = 3
        val sudoku = solution.copy()
        val backup = ArrayDeque<Triple<Int, Int, String?>>()

        var initialNumbsTaken = 30
        do {
            val row = Random.nextInt(0..8)
            val col = Random.nextInt(0..8)
            if (sudoku[row][col] != null) {
                backup.addLast(Triple(row, col, sudoku[row][col]))
                initialNumbsTaken -= 1
            }
        } while(initialNumbsTaken > 0)

        while (attempts > 0) {
            var row: Int
            var col: Int

            do {
                row = Random.nextInt(0..8)
                col = Random.nextInt(0..8)
            } while(sudoku[row][col] == null)
            backup.addLast(Triple(row, col, sudoku[row][col]))
            sudoku[row][col] = null

            var trySolve = sudoku.copy()
            while (checkNumSolutions(trySolve) != 1) {
                val stackTop = backup.removeLast()
                sudoku[stackTop.first][stackTop.second] = stackTop.third
                trySolve = sudoku.copy()
                attempts -= 1
            }
        }

        for (i in 0..8) {
            for (j in 0..8) {
                fields[i][j].postValue(
                    fields[i][j].value!!.copy(
                        isEnabled = sudoku[i][j] == null,
                        notes = fields[i][j].value!!.notes.clone(),
                        number = sudoku[i][j],
                        solution = solution[i][j]
                    )
                )
            }
        }
    }

    fun getField(row: Int, col: Int): MutableLiveData<SudokuField> {
        return fields[row][col]
    }

    fun setFieldNumber(s: String, isNote: Boolean) {
        if (currFieldCoords == null) return
        val currField = fields[currFieldCoords!!.first][currFieldCoords!!.second]

        if (isNote) {
            val notes = currField.value!!.notes.clone()
            notes[s.toInt() - 1] = if(notes[s.toInt() - 1] == null) s else null
            currField.postValue(currField.value!!.copy(
                notes = notes
            ))
        } else {
            currField.postValue(
                currField.value!!.copy(
                number = if(currField.value!!.number != s) s else null,
                notes = currField.value!!.notes.clone()
            ))
            for (field in SudokuUtil.getRelevantValues(fields, currFieldCoords!!.first, currFieldCoords!!.second)) {
                val newNotes = field.value!!.notes.clone()
                newNotes[s.toInt() - 1] = null
                field.postValue(
                    field.value!!.copy(
                        notes = newNotes
                    )
                )
            }
        }
    }

    fun fieldSelected(row: Int, col: Int) {
        currFieldCoords = Pair(row, col)

        val toBeHighlighted = SudokuUtil.getRelevantValues(fields, row, col)

        if (lastHighlighted != null) {
            for (field in lastHighlighted!!) {
                field.postValue(field.value!!.copy(
                    isHighlighted = false,
                    notes = field.value!!.notes.clone()
                ))
            }
        }

        for (field in toBeHighlighted) {
            field.postValue(field.value!!.copy(
                isHighlighted = true,
                notes = field.value!!.notes.clone()
            ))
        }

        lastHighlighted = toBeHighlighted
    }

    private fun Array<Array<String?>>.copy() = Array(size) { get(it).clone() }

    private fun checkGrid(grid: Array<Array<String?>>): Boolean {
        for (row in grid) {
            if (null in row) {
                return false
            }
        }
        return true
    }

    private fun makeSolution(): Boolean {
        var row = 0
        var col = 0
        for (i in 0..81) {
            row = kotlin.math.floor(i / 9.0).toInt()
            col = i % 9
            if (solution[row][col] == null) {
                symbols.shuffle()
                for (value in symbols) {
                    if (value !in SudokuUtil.getRelevantValues(solution, row, col)) {
                        solution[row][col] = value
                        if (checkGrid(solution)) {
                            return true
                        }
                        else if (makeSolution()) {
                            return true
                        }

                    }
                }
                break
            }
        }
        solution[row][col] = null
        return false
    }

    private fun checkNumSolutions(grid: Array<Array<String?>>): Int {
        var numSolutions = 0
        fun checkNumSolutionsRec(grid: Array<Array<String?>>) {
            var row = 0
            var col = 0
            for (i in 0..81) {
                row = kotlin.math.floor(i / 9.0).toInt()
                col = i % 9
                if (grid[row][col] == null) {
                    for (value in symbols) {
                        if (value !in SudokuUtil.getRelevantValues(grid, row, col)) {
                            grid[row][col] = value
                            if (checkGrid(grid)) {
                                numSolutions += 1
                                break
                            }
                            checkNumSolutionsRec(grid)

                        }
                    }
                    break
                }
            }
            grid[row][col] = null
        }
        checkNumSolutionsRec(grid)
        return numSolutions
    }
}

data class SudokuField(
    val isEnabled: Boolean,
    val isHighlighted: Boolean,
    val solution: String? = null,
    val number: String? = null,
    val notes: Array<String?> = arrayOfNulls(9)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SudokuField

        if (isEnabled != other.isEnabled) return false
        if (isHighlighted != other.isHighlighted) return false
        if (number != other.number) return false
        if (!notes.contentEquals(other.notes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isEnabled.hashCode()
        result = 31 * result + isHighlighted.hashCode()
        result = 31 * result + (solution?.hashCode() ?: 0)
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + notes.contentHashCode()
        return result
    }
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}