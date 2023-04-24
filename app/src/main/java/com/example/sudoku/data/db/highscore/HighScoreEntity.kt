package com.example.sudoku.data.db.highscore

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sudoku.viewmodel.Difficulty

@Entity(tableName = "highscore")
data class HighScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val difficulty: Difficulty,
    val time: Long
)
