package com.example.sudoku

import androidx.lifecycle.MutableLiveData

class SudokuLogic {
    private var difficulty: Difficulty? = null
    private val fields: Array<Array<MutableLiveData<SudokuField>>>
    private var sudoku: Array<Array<String?>>
    private val symbols = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    private var lastHighlighted: Array<MutableLiveData<SudokuField>>? = null

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
        sudoku = Array(9) { arrayOfNulls<String>(9) }
    }

    fun newGame(difficulty: Difficulty? = null) {
        this.difficulty = difficulty
        sudoku = Array(9) { arrayOfNulls<String>(9) }
        makeSolution()
        for (i in 0..8) {
            for (j in 0..8) {
                fields[i][j].postValue(
                    fields[i][j].value!!.copy(
                        notes = fields[i][j].value!!.notes.clone(),
                        number = sudoku[i][j]
                    )
                )
            }
        }
        //val solution = sudoku.copy()
    }

    fun getField(row: Int, col: Int): MutableLiveData<SudokuField> {
        return fields[row][col]
    }

    fun fieldSelected(row: Int, col: Int) {
        val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
        val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()

        val rowArr = fields[row]
        val colArr = Array(9) {fields[it][col]}
        val square = fields[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                fields[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                fields[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()
        val toBeHighLighted = rowArr + colArr + square

        if (lastHighlighted != null) {
            for (field in lastHighlighted!!) {
                field.postValue(field.value!!.copy(
                    isHighlighted = false,
                    notes = field.value!!.notes.clone()
                ))
            }
        }

        for (field in toBeHighLighted) {
            field.postValue(field.value!!.copy(
                isHighlighted = true,
                notes = field.value!!.notes.clone()
            ))
        }

        lastHighlighted = toBeHighLighted
    }

    private fun Array<Array<String?>>.copy() = Array(size) { get(it).clone() }

    private fun checkGrid(): Boolean {
        for (row in sudoku) {
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
            if (sudoku[row][col] == null) {
                symbols.shuffle()
                for (value in symbols) {
                    if (value !in sudoku[row] && value !in Array(9) { sudoku[it][col] }) {
                        val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
                        val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()
                        val square = sudoku[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                                sudoku[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                                sudoku[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()
                        if (value !in square) {
                            sudoku[row][col] = value
                            if (checkGrid()) {
                                return true
                            }
                            else if (makeSolution()) {
                                return true
                            }
                        }
                    }
                }
                break
            }
        }
        sudoku[row][col] = null
        return false
    }

    private fun checkNumSolutions(grid: Array<Array<String?>>): Int {
        var numSolutions = 0
        fun checkNumSolutionsRec(grid: Array<Array<String?>>): Boolean {
            var row = 0
            var col = 0
            for (i in 0..81) {
                row = kotlin.math.floor(i / 9.0).toInt()
                col = i % 9
                if (grid[row][col] == null) {
                    for (value in symbols) {
                        if (value !in grid[row] && value !in Array<String?>(9) { grid[it][col] }) {
                            val fromRow = (kotlin.math.floor(row / 3.0) * 3).toInt()
                            val fromCol = (kotlin.math.floor(col / 3.0) * 3).toInt()
                            val square = grid[fromRow].slice(fromCol..(fromCol + 2)).toTypedArray() +
                                    grid[fromRow + 1].slice(fromCol..(fromCol + 2)).toTypedArray() +
                                    grid[fromRow + 2].slice(fromCol..(fromCol + 2)).toTypedArray()
                            if (value !in square) {
                                grid[row][col] = value
                                if (checkGrid()) {
                                    numSolutions += 1
                                    break
                                }
                                else if (checkNumSolutionsRec(grid)) {
                                    return true
                                }
                            }
                        }
                    }
                    break
                }
            }
            grid[row][col] = null
            return false
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
    val notes: Array<Int?> = arrayOfNulls<Int>(9)
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