package com.example.sudoku.data.db

import com.example.sudoku.data.db.highscore.HighScoreEntity
import com.example.sudoku.data.db.sudoku.SudokuEntity
import com.example.sudoku.viewmodel.Difficulty
import kotlinx.coroutines.flow.Flow

interface DBRepository {
    suspend fun insertSudoku(sudoku: SudokuEntity)

    suspend fun deleteSudoku(sudoku: SudokuEntity)

    fun getSudoku(difficulty: Difficulty): Flow<SudokuEntity?>

    fun getSudokuAmount(difficulty: Difficulty): Flow<Int>

    suspend fun insertHighScore(highScore: HighScoreEntity)

    suspend fun deleteHighScore(highScore: HighScoreEntity)

    suspend fun updateHighScore(highScore: HighScoreEntity)

    fun getHighScore(difficulty: Difficulty): Flow<HighScoreEntity?>
}