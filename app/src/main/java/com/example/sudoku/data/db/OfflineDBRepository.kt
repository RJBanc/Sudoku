package com.example.sudoku.data.db

import com.example.sudoku.data.db.highscore.HighScoreDao
import com.example.sudoku.data.db.highscore.HighScoreEntity
import com.example.sudoku.data.db.sudoku.SudokuDao
import com.example.sudoku.data.db.sudoku.SudokuEntity
import com.example.sudoku.viewmodel.Difficulty
import kotlinx.coroutines.flow.Flow

class OfflineDBRepository(
    private val sudokuDao: SudokuDao,
    private val highScoreDao: HighScoreDao
) : DBRepository {
    override suspend fun insertSudoku(sudoku: SudokuEntity) = sudokuDao.insert(sudoku)

    override suspend fun deleteSudoku(sudoku: SudokuEntity) = sudokuDao.delete(sudoku)

    override fun getSudoku(difficulty: Difficulty): Flow<SudokuEntity?> = sudokuDao.getSudoku(difficulty)

    override fun getSudokuAmount(difficulty: Difficulty): Flow<Int> = sudokuDao.getSudokuAmount(difficulty)

    override suspend fun insertHighScore(highScore: HighScoreEntity) = highScoreDao.insert(highScore)

    override suspend fun deleteHighScore(highScore: HighScoreEntity) = highScoreDao.delete(highScore)

    override suspend fun updateHighScore(highScore: HighScoreEntity) = highScoreDao.update(highScore)

    override fun getHighScore(difficulty: Difficulty): Flow<HighScoreEntity?> = highScoreDao.getHighScore(difficulty)
}