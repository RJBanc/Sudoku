package com.example.sudoku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.backup.BackupGame
import com.example.sudoku.data.backup.BackupHistory
import com.example.sudoku.data.backup.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import kotlin.random.Random
import kotlin.random.nextInt

class SudokuViewModel(application: Application) : AndroidViewModel(application) {
    var difficulty: Difficulty = Difficulty.EASY
    var isRunning: Boolean = false
    private val fields: Array<Array<MutableLiveData<SudokuField>>>
    private var solution: Array<Array<String?>>
    private val symbols = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    private var lastHighlighted: Array<MutableLiveData<SudokuField>>? = null
    private var currFieldCoords: Pair<Int, Int>? = null
    private val history: ArrayDeque<Triple<Pair<Int, Int>, String?, List<Int>>> = ArrayDeque()

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

    init {
        fields  = Array(9) {
            Array(9) {
                MutableLiveData<SudokuField>(
                    SudokuField(
                        isEnabled = true,
                        isHighlighted = false,
                        isSelected = false
                    )
                )
            }
        }
        solution = Array(9) { arrayOfNulls(9) }
    }

    fun startGame() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                restoreBackup()
            } catch(e: FileNotFoundException) {
                newGame()
            }
        }
    }

    fun startNewGame(difficulty: Difficulty = this.difficulty) {
        viewModelScope.launch(Dispatchers.Default) {
            newGame(difficulty)
        }
    }

    fun newGame(difficulty: Difficulty = this.difficulty) {
        this.difficulty = difficulty

        val difficultyRange = mapOf(
            Difficulty.BEGINNER to 3600..4500,
            Difficulty.EASY to 4300..5500,
            Difficulty.MEDIUM to 5300..6900,
            Difficulty.HARD to 6500..9300,
            Difficulty.EVIL to 8300..14000,
            Difficulty.DIABOLICAL to 11000..25000
        )

        history.clear()
        val sudoku = Array(9) { arrayOfNulls<String>(9) }
        val backup = ArrayDeque<Triple<Int, Int, String?>>()
        val initialNumbsTaken = 40

        prepareSudoku(sudoku, backup, initialNumbsTaken)

        while (true) {
            var difficultyScore = difficultyRange[this.difficulty]!!.first
            var attempts = 5
            do {
                if (difficultyScore > difficultyRange[this.difficulty]!!.last) {
                    val addVal = backup.removeLast()
                    sudoku[addVal.first][addVal.second] = addVal.third
                } else if (difficultyScore < difficultyRange[this.difficulty]!!.first)
                    removeRandomNum(sudoku, backup)


                val solver = SudokuSolver(sudoku.copy())
                if (!solver.solve()) {
                    if (attempts-- == 0) {
                        attempts = 5
                        difficultyScore = difficultyRange[this.difficulty]!!.first
                        prepareSudoku(sudoku, backup, initialNumbsTaken)
                        continue
                    }
                    val addVal = backup.removeLast()
                    sudoku[addVal.first][addVal.second] = addVal.third
                } else if (solver.finished()) {
                    difficultyScore = solver.difficultyScore()
                }
            } while (difficultyScore !in difficultyRange[this.difficulty]!!)

            if (checkNumSolutions(sudoku.copy()) == 1) break
            do {
                val addVal = backup.removeLast()
                sudoku[addVal.first][addVal.second] = addVal.third
            } while (checkNumSolutions(sudoku.copy()) != 1)
        }

        for (i in 0..8) {
            for (j in 0..8) {
                fields[i][j].postValue(fields[i][j].value!!.copy(
                    isEnabled = sudoku[i][j] == null,
                    number = sudoku[i][j],
                    solution = solution[i][j]
                ))
            }
        }

        isRunning = true

    }

    fun getField(row: Int, col: Int): MutableLiveData<SudokuField> {
        return fields[row][col]
    }

    fun setFieldNumber(s: String, isNote: Boolean) {
        if (currFieldCoords == null) return
        val currField = fields[currFieldCoords!!.first][currFieldCoords!!.second]
        if (!currField.value!!.isEnabled) return

        history.addLast(Triple(
            Pair(currFieldCoords!!.first, currFieldCoords!!.second),
            currField.value!!.number,
            SudokuUtil.getRelevantValues(fields, currFieldCoords!!.first, currFieldCoords!!.second)
                .map { it.value!!.notes }
        ))

        if (isNote) {
            var notes = currField.value!!.notes
            val sBit = stringToBitMap[s]!!
            notes = if(sBit and notes > 0) BitUtil.removeBits(notes, sBit) else notes or sBit
            currField.value = currField.value!!.copy(
                notes = notes
            )
        } else {
            for (field in SudokuUtil.getRelevantValues(fields, currFieldCoords!!.first, currFieldCoords!!.second)) {
                var newNotes = field.value!!.notes
                newNotes = BitUtil.removeBits(newNotes, stringToBitMap[s]!!)
                field.value = field.value!!.copy(
                    notes = newNotes
                )
            }
            currField.value =
                currField.value!!.copy(
                    number = if(currField.value!!.number != s) s else null,
            )
        }
    }

    fun fieldSelected(row: Int, col: Int) {
        currFieldCoords = Pair(row, col)

        val toBeHighlighted = SudokuUtil.getRelevantValues(fields, row, col).toMutableList()

        if (lastHighlighted != null) {
            for (field in lastHighlighted!!) {
                field.value = field.value!!.copy(
                    isHighlighted = false,
                    isSelected = false,
                )
            }
        }

        if (fields[row][col].value!!.number != null) {
            for (i in 0..8) {
                for (j in 0..8) {
                    if (fields[i][j].value!!.number == fields[row][col].value!!.number &&
                            fields[i][j] !in toBeHighlighted) {
                        fields[i][j].value = fields[i][j].value!!.copy(
                            isHighlighted = true,
                            isSelected = false,
                        )
                        toBeHighlighted.add(fields[i][j])
                    }
                }
            }
        }

        for (field in toBeHighlighted) {
            field.value = field.value!!.copy(
                isHighlighted = true,
                isSelected = false,
            )
        }

        fields[row][col].value = fields[row][col].value!!.copy(
            isHighlighted = false,
            isSelected = true,
        )

        lastHighlighted = toBeHighlighted.toTypedArray()
    }

    fun undo() {
        val lastMove = history.removeLastOrNull() ?: return

        for ((i, field) in SudokuUtil.getRelevantValues(fields, lastMove.first.first, lastMove.first.second).withIndex()) {
            field.value = field.value!!.copy(
                notes = lastMove.third[i]
            )
        }

        val field = fields[lastMove.first.first][lastMove.first.second]
        field.value = field.value!!.copy(
            number = lastMove.second
        )
    }

    fun createBackup() {
        var puzzle = ""
        val editable = mutableListOf<Boolean>()
        val notes = mutableListOf<Int>()

        for (i in fields.indices) {
            for (j in fields[0].indices) {
                val fieldVal = fields[i][j].value!!
                notes.add(fieldVal.notes)
                puzzle += fieldVal.number ?: "0"
                editable.add(fieldVal.isEnabled)
            }
        }

        val backup = BackupGame(
            difficulty = this.difficulty,
            solution = this.solution.joinToString(separator = "") {
                it.joinToString(separator = "")
            },
            puzzle = puzzle,
            editable = editable.toList(),
            notes = notes.toList(),
            history = List(this.history.size) { BackupHistory(this.history[it]) }
        )

        viewModelScope.launch(Dispatchers.Default) {
            BackupManager(getApplication()).createBackup(backup)
        }
    }

    suspend fun restoreBackup() {
        val backup =  viewModelScope.async {
            BackupManager(getApplication()).restoreBackup()
        }

        difficulty = backup.await().difficulty
        lastHighlighted = null
        currFieldCoords = null

        solution = Array(9) { it ->
            Array(9) { jt ->
                val numb = backup.await().solution[it * 9 + jt].toString()
                if (numb != "0") numb else null
            }
        }

        history.clear()
        for (entry in backup.await().history) history.addLast(entry.getHistory())

        for (i in fields.indices) {
            for (j in fields[0].indices) {
                val listIndex = i * 9 + j
                fields[i][j].postValue(fields[i][j].value!!.copy(
                    isEnabled = backup.await().editable[listIndex],
                    isHighlighted = false,
                    isSelected = false,
                    solution = backup.await().solution[listIndex].toString(),
                    number = if (backup.await().puzzle[listIndex] != '0')
                        backup.await().puzzle[listIndex].toString()
                    else
                        null,
                    notes = backup.await().notes[listIndex]
                ))
            }
        }

        isRunning = true
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

    private fun prepareSudoku(sudoku: Array<Array<String?>>, backup: ArrayDeque<Triple<Int, Int, String?>>, initialNumbsTaken: Int = 30) {
        solution = Array(9) { arrayOfNulls(9) }
        makeSolution()

        backup.clear()
        for (i in 0..8) {
            for (j in 0..8) {
                sudoku[i][j] = solution[i][j]
            }
        }

        var numbsTaken = initialNumbsTaken
        do {
            removeRandomNum(sudoku, backup)
            numbsTaken--
        } while(numbsTaken > 0)
    }

    private fun removeRandomNum(sudoku: Array<Array<String?>>, backup: ArrayDeque<Triple<Int, Int, String?>>) {
        if (!sudoku.any { row -> row.any { it != null } }) throw NoSuchElementException()

        while (true) {
            val row = Random.nextInt(0..8)
            val col = Random.nextInt(0..8)

            if (sudoku[row][col] == null) continue
            backup.addLast(Triple(row, col, sudoku[row][col]))
            sudoku[row][col] = null
            return
        }
    }

    internal fun makeSolution(): Boolean {
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

     internal fun checkNumSolutions(grid: Array<Array<String?>>): Int {
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
    val isSelected: Boolean,
    val solution: String? = null,
    val number: String? = null,
    val notes: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SudokuField

        if (isEnabled != other.isEnabled) return false
        if (isHighlighted != other.isHighlighted) return false
        if (isSelected != other.isHighlighted) return false
        if (solution != other.solution) return false
        if (number != other.number) return false
        if (notes  != other.notes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isEnabled.hashCode()
        result = 31 * result + isHighlighted.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + (solution?.hashCode() ?: 0)
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + notes.hashCode()
        return result
    }
}

enum class Difficulty {
    BEGINNER,
    EASY,
    MEDIUM,
    HARD,
    EVIL,
    DIABOLICAL
}