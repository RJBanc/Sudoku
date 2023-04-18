package com.example.sudoku

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sudoku.ui.GameScreen
import com.example.sudoku.ui.theme.SudokuTheme
import com.example.sudoku.viewmodel.SudokuViewModel

class MainActivity : ComponentActivity() {
    private val sudoku: SudokuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
                this.window.statusBarColor = MaterialTheme.colors.background.toArgb()
                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !isSystemInDarkTheme()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GameScreen(sudokuGame = sudoku)
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()

        if (!sudoku.instanciated)
            sudoku.startGame()
    }

    override fun onPause() {
        super.onPause()

        if (sudoku.isRunning.value == true)
            sudoku.pauseGame()

        if (sudoku.instanciated)
            sudoku.createBackup()
    }
}