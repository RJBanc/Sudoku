package com.example.sudoku.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sudoku.SudokuApplication
import com.example.sudoku.core.SudokuSolver
import com.example.sudoku.data.backup.BackupGame
import com.example.sudoku.data.backup.BackupHistory
import com.example.sudoku.data.backup.BackupManager
import com.example.sudoku.data.db.DBRepository
import com.example.sudoku.data.db.highscore.HighScoreEntity
import com.example.sudoku.data.db.sudoku.SudokuEntity
import com.example.sudoku.util.BitUtil
import com.example.sudoku.util.SudokuUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class SudokuViewModel(application: Application) : AndroidViewModel(application) {
    var difficulty: Difficulty = Difficulty.EASY
    val isRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    val timeElapsed: MutableLiveData<Long> = MutableLiveData(0L)
    val isCompleted: MutableLiveData<Boolean> = MutableLiveData(false)
    var instanciated: MutableLiveData<Boolean> = MutableLiveData(false)
    private var unsolvedFields = 81
    private val symbols = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
    private var lastHighlighted: MutableList<MutableLiveData<SudokuField>>? = null
    private var currFieldCoords: Pair<Int, Int>? = null
    private val history: ArrayDeque<Triple<Pair<Int, Int>, String?, List<Int>>> = ArrayDeque()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val repository: DBRepository = getApplication<SudokuApplication>().container.sudokuRepository
    private val fields: Array<Array<MutableLiveData<SudokuField>>> = Array(9) {
        Array(9) {
            MutableLiveData<SudokuField>(
                SudokuField()
            )
        }
    }

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

    fun startGame() {
        sudokuSupply()
        viewModelScope.launch {
            try {
                restoreBackup()
            } catch(e: Exception) {
                newGame(initialNotes = getApplication<SudokuApplication>()
                    .userPreferencesRepository
                    .createInitialNotes
                    .firstOrNull() ?: false)
            }
            instanciated.postValue(true)
        }
    }

    fun startNewGame(difficulty: Difficulty = this.difficulty, initialNotes: Boolean = false) {
        viewModelScope.launch {
            newGame(difficulty, initialNotes)
        }
    }

    fun pauseGame() {
        isRunning.postValue(false)

        handler.removeCallbacksAndMessages(null)
    }

    fun resumeGame() {
        if (isRunning.value == true) return

        isRunning.postValue(true)

        handler.postDelayed(object : Runnable {
            override fun run() {
                timeElapsed.postValue((timeElapsed.value ?: 0L) + 1)
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    fun getField(row: Int, col: Int): MutableLiveData<SudokuField> {
        return fields[row][col]
    }

    fun setFieldNumber(s: String, isNote: Boolean) {
        if (isRunning.value == false) return
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
            if (currField.value!!.number == currField.value!!.solution)
                unsolvedFields += 1
            else if (s == currField.value!!.solution)
                unsolvedFields -= 1

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

        checkGameFinished()
    }

    fun fieldSelected(row: Int, col: Int) {
        if (isRunning.value == false) return

        viewModelScope.launch {
            currFieldCoords = Pair(row, col)
            val toBeHighlighted = SudokuUtil.getRelevantValues(fields, row, col).toMutableList()

            if (fields[row][col].value!!.number != null) {
                for (i in 0..8) {
                    for (j in 0..8) {
                        if (fields[i][j].value!!.number == fields[row][col].value!!.number &&
                            fields[i][j] !in toBeHighlighted)
                            toBeHighlighted.add(fields[i][j])
                    }
                }
            }

            if (lastHighlighted != null) {
                for (field in lastHighlighted!!) {
                    if (field in toBeHighlighted) continue
                    field.postValue(field.value!!.copy(
                        isHighlighted = false,
                        isSelected = false,
                    ))
                }
            }

            for (field in toBeHighlighted) {
                field.postValue(field.value!!.copy(
                    isHighlighted = true,
                    isSelected = false,
                ))
            }

            fields[row][col].postValue(fields[row][col].value!!.copy(
                isHighlighted = false,
                isSelected = true,
            ))

            lastHighlighted = toBeHighlighted
        }
    }

    fun undo() {
        if (isRunning.value == false) return
        val lastMove = history.removeLastOrNull() ?: return

        for ((i, field) in SudokuUtil.getRelevantValues(fields, lastMove.first.first, lastMove.first.second).withIndex()) {
            field.value = field.value!!.copy(
                notes = lastMove.third[i]
            )
        }

        val field = fields[lastMove.first.first][lastMove.first.second]

        if (lastMove.second == field.value!!.number) return
        if (lastMove.second == field.value!!.solution) unsolvedFields -= 1
        else if (field.value!!.number == field.value!!.solution) unsolvedFields += 1

        field.value = field.value!!.copy(
            number = lastMove.second
        )
    }

    fun getHighScore(): StateFlow<Long> {
        return repository.getHighScore(difficulty).map { it?.time ?: Long.MAX_VALUE  }
            .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = Long.MAX_VALUE
        )
    }

    fun createBackup() {
        viewModelScope.launch {
            var puzzle = ""
            var solution = ""
            val editable = mutableListOf<Boolean>()
            val notes = mutableListOf<Int>()

            for (i in fields.indices) {
                for (j in fields[0].indices) {
                    val fieldVal = fields[i][j].value!!
                    notes.add(fieldVal.notes)
                    puzzle += fieldVal.number ?: "0"
                    solution += fieldVal.solution ?: "0"
                    editable.add(fieldVal.isEnabled)
                }
            }

            val backup = BackupGame(
                difficulty = difficulty,
                solution = solution,
                puzzle = puzzle,
                editable = editable.toList(),
                notes = notes.toList(),
                history = List(history.size) { BackupHistory(history[it]) },
                timeElapse = timeElapsed.value ?: 0L,
                unsolvedFields = unsolvedFields
            )


            BackupManager(getApplication()).createBackup(backup)
        }
    }

    private suspend fun restoreBackup() {
        val backup = BackupManager(getApplication()).restoreBackup()

        difficulty = backup.difficulty
        timeElapsed.postValue(backup.timeElapse)
        isCompleted.postValue(false)
        lastHighlighted = null
        currFieldCoords = null
        unsolvedFields = backup.unsolvedFields

        history.clear()
        for (entry in backup.history) history.addLast(entry.getHistory())

        for (i in fields.indices) {
            for (j in fields[0].indices) {
                val listIndex = i * 9 + j
                fields[i][j].postValue(fields[i][j].value!!.copy(
                    isEnabled = backup.editable[listIndex],
                    isHighlighted = false,
                    isSelected = false,
                    solution = backup.solution[listIndex].toString(),
                    number = if (backup.puzzle[listIndex] != '0')
                        backup.puzzle[listIndex].toString()
                    else
                        null,
                    notes = backup.notes[listIndex]
                ))
            }
        }

        resumeGame()
    }

    private fun checkGameFinished() {
        if (unsolvedFields > 0) return

        pauseGame()
        isCompleted.value = true
        viewModelScope.launch(Dispatchers.Default) {
            BackupManager(getApplication()).deleteBackup()
        }
        updateHighScore()
    }

    private fun updateHighScore() {
        if (isCompleted.value != true) return

        viewModelScope.launch {
            val currentHS = repository.getHighScore(difficulty).firstOrNull()

            if (currentHS == null)
                repository.insertHighScore(HighScoreEntity(
                    difficulty = difficulty,
                    time = timeElapsed.value ?: Long.MAX_VALUE
                ))
            else
                repository.updateHighScore(currentHS.copy(
                    time = min(timeElapsed.value ?: Long.MAX_VALUE, currentHS.time)
                ))
        }
    }

    private fun Array<Array<String?>>.copy() = Array(size) { get(it).clone() }

    private suspend fun newGame(difficulty: Difficulty = this.difficulty, initialNotes: Boolean = false) {
        this.difficulty = difficulty
        history.clear()

        val sudoku = Array(9) { arrayOfNulls<String>(9) }
        val solution = Array(9) { arrayOfNulls<String>(9) }

        if ((repository.getSudokuAmount(difficulty).firstOrNull() ?: 0) == 0)
            createSudoku(sudoku, solution, difficulty)
        else
            loadSudoku(sudoku, solution, difficulty)

        val noteArr = Array(9) { Array(9) { 0b111111111 } }
        unsolvedFields = 81
        for (i in 0..8) {
            for (j in 0..8) {
                if (sudoku[i][j] == null) continue

                unsolvedFields -= 1
                if (initialNotes) {
                    SudokuUtil.applyToRelevantValues(noteArr, i, j) {
                        BitUtil.removeBits(
                            it, 1 shl (sudoku[i][j]!!.toInt() - 1)
                        )
                    }
                }
            }
        }


        for (i in 0..8) {
            for (j in 0..8) {
                fields[i][j].postValue(SudokuField(
                    isEnabled = sudoku[i][j] == null,
                    number = sudoku[i][j],
                    solution = solution[i][j],
                    notes = if (initialNotes) noteArr[i][j] else 0
                ))
            }
        }

        timeElapsed.postValue(0L)
        isCompleted.postValue(false)
        resumeGame()
    }

    private fun createSudoku(
        sudoku: Array<Array<String?>>,
        solution: Array<Array<String?>>,
        difficulty: Difficulty
    ) {
        val difficultyRange = mapOf(
            Difficulty.BEGINNER to 3600..4500,
            Difficulty.EASY to 4300..5500,
            Difficulty.MEDIUM to 5300..6900,
            Difficulty.HARD to 6500..9300,
            Difficulty.EVIL to 8300..14000,
            Difficulty.DIABOLICAL to 11000..25000
        )
        val backup = ArrayDeque<Triple<Int, Int, String?>>()

        prepareSudoku(sudoku, solution, backup)

        while (true) {
            var difficultyScore = difficultyRange[difficulty]!!.first
            var attempts = 5
            do {
                if (difficultyScore > difficultyRange[difficulty]!!.last) {
                    val addVal = backup.removeLast()
                    sudoku[addVal.first][addVal.second] = addVal.third
                } else if (difficultyScore < difficultyRange[difficulty]!!.first)
                    removeRandomNum(sudoku, backup)


                val solver = SudokuSolver(sudoku.copy())
                if (!solver.solve()) {
                    if (attempts-- == 0) {
                        attempts = 5
                        difficultyScore = difficultyRange[difficulty]!!.first
                        prepareSudoku(sudoku, solution, backup)
                        continue
                    }
                    val addVal = backup.removeLast()
                    sudoku[addVal.first][addVal.second] = addVal.third
                } else if (solver.finished()) {
                    difficultyScore = solver.difficultyScore()
                }
            } while (difficultyScore !in difficultyRange[difficulty]!!)

            if (checkNumSolutions(sudoku.copy()) == 1) break
            do {
                val addVal = backup.removeLast()
                sudoku[addVal.first][addVal.second] = addVal.third
            } while (checkNumSolutions(sudoku.copy()) != 1)
        }
    }

    private suspend fun loadSudoku(
        sudoku: Array<Array<String?>>,
        solution: Array<Array<String?>>,
        difficulty: Difficulty
    ) {
        val sudokuEntity = repository.getSudoku(difficulty).first()!!

        for (i in 0..8) {
            for (j in 0..8) {
                val listIndex = i * 9 + j

                sudoku[i][j] = if (sudokuEntity.puzzle[listIndex] != '0')
                    sudokuEntity.puzzle[listIndex].toString()
                else
                    null

                solution[i][j] = if (sudokuEntity.solution[listIndex] != '0')
                    sudokuEntity.solution[listIndex].toString()
                else
                    null
            }
        }

        repository.deleteSudoku(sudokuEntity)
    }

    private fun sudokuSupply() {
        viewModelScope.launch {
            while(true) {
                for (difficulty in Difficulty.values()) {
                    if ((repository.getSudokuAmount(difficulty).firstOrNull() ?: 0) >= 20) continue

                    val sudoku = Array(9) { arrayOfNulls<String>(9) }
                    val solution = Array(9) { arrayOfNulls<String>(9) }

                    createSudoku(sudoku, solution, difficulty)

                    var sudokuStr = ""
                    var solutionStr = ""

                    for (i in 0..8) {
                        for (j in 0..8) {
                            sudokuStr += sudoku[i][j] ?: "0"
                            solutionStr += solution[i][j] ?: "0"
                        }
                    }

                    repository.insertSudoku(SudokuEntity(
                        puzzle = sudokuStr,
                        solution = solutionStr,
                        difficulty = difficulty
                    ))
                }
            }
        }
    }

    private fun checkGrid(grid: Array<Array<String?>>): Boolean {
        for (row in grid) {
            if (null in row) {
                return false
            }
        }
        return true
    }

    private fun prepareSudoku(
        sudoku: Array<Array<String?>>,
        solution: Array<Array<String?>>,
        backup: ArrayDeque<Triple<Int, Int, String?>>
    ) {
        for (i in 0..8) {
            for (j in 0..8) {
                solution[i][j] = null
            }
        }
        makeSolution(solution)

        backup.clear()
        for (i in 0..8) {
            for (j in 0..8) {
                sudoku[i][j] = solution[i][j]
            }
        }

        var numbsTaken = 40
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

    private fun makeSolution(solution: Array<Array<String?>>): Boolean {
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
                        else if (makeSolution(solution)) {
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
    val isEnabled: Boolean = false,
    val isHighlighted: Boolean = false,
    val isSelected: Boolean = false,
    val solution: String? = null,
    val number: String? = null,
    val notes: Int = 0
)

enum class Difficulty {
    BEGINNER,
    EASY,
    MEDIUM,
    HARD,
    EVIL,
    DIABOLICAL
}