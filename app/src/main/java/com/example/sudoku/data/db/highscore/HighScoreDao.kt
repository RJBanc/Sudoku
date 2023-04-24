package com.example.sudoku.data.db.highscore

import androidx.room.*
import com.example.sudoku.viewmodel.Difficulty
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(highscore: HighScoreEntity)

    @Delete
    suspend fun delete(highscore: HighScoreEntity)

    @Update
    suspend fun update(highscore: HighScoreEntity)

    @Query("SELECT * FROM highscore WHERE difficulty = :difficulty")
    fun getHighScore(difficulty: Difficulty): Flow<HighScoreEntity>
}