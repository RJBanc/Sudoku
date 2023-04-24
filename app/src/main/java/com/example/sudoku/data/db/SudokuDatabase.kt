package com.example.sudoku.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sudoku.data.db.highscore.HighScoreDao
import com.example.sudoku.data.db.highscore.HighScoreEntity
import com.example.sudoku.data.db.sudoku.SudokuDao
import com.example.sudoku.data.db.sudoku.SudokuEntity

@Database(
    entities = [SudokuEntity::class, HighScoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {

    abstract fun sudokuDao(): SudokuDao
    abstract fun highScoreDao(): HighScoreDao

    companion object {
        @Volatile
        private var Instance: SudokuDatabase? = null

        fun getDatabase(context: Context): SudokuDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = SudokuDatabase::class.java,
                    name = "sudoku_database"
                ).fallbackToDestructiveMigration().build().also {
                    Instance = it
                }
            }
        }
    }
}