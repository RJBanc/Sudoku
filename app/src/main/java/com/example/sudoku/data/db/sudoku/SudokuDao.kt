package com.example.sudoku.data.db.sudoku

import androidx.room.*
import com.example.sudoku.viewmodel.Difficulty
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudoku: SudokuEntity)

    @Delete
    suspend fun delete(sudoku: SudokuEntity)

    @Query("SELECT * FROM sudokus WHERE difficulty = :difficulty LIMIT 1")
    fun getSudoku(difficulty: Difficulty): Flow<SudokuEntity>

    @Query("SELECT COUNT(id) FROM sudokus WHERE difficulty = :difficulty")
    fun getSudokuAmount(difficulty: Difficulty): Flow<Int>
}