package com.example.sudoku.data.db.sudoku

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sudoku.viewmodel.Difficulty

@Entity(tableName = "sudokus")
data class SudokuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solution: String,
    val puzzle: String,
    val difficulty: Difficulty
)