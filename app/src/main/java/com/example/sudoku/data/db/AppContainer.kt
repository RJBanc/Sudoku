package com.example.sudoku.data.db

import android.content.Context

interface AppContainer {
    val sudokuRepository: DBRepository
}

class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val sudokuRepository: DBRepository by lazy {
        OfflineDBRepository(
            SudokuDatabase.getDatabase(context).sudokuDao(),
            SudokuDatabase.getDatabase(context).highScoreDao()
        )
    }
}