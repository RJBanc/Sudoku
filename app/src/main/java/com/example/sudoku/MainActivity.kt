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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.ui.SudokuNavigation
import com.example.sudoku.ui.theme.SudokuTheme
import com.example.sudoku.viewmodel.SettingsViewModel
import com.example.sudoku.viewmodel.SudokuViewModel

class MainActivity : ComponentActivity() {
    private val sudoku: SudokuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settings: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

            val darkMode = when(settings.nightMode.collectAsState().value) {
                "on" -> true
                "off" -> false
                "system" -> isSystemInDarkTheme()
                else -> false
            }

            SudokuTheme(darkTheme = darkMode) {
                // A surface container using the 'background' color from the theme
                if (settings.screenOn.collectAsState().value)
                    this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else
                    this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                this.window.statusBarColor = MaterialTheme.colors.background.toArgb()
                WindowInsetsControllerCompat(window, window.decorView)
                    .isAppearanceLightStatusBars = !darkMode

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SudokuNavigation(sudokuViewModel = sudoku)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val settings: SettingsViewModel by viewModels(
            factoryProducer = { SettingsViewModel.Factory }
        )

        if (!sudoku.instanciated)
            sudoku.startGame()
    }

    override fun onPause() {
        super.onPause()

        if (sudoku.isRunning.value == true)
            sudoku.pauseGame()

        if (sudoku.instanciated && !sudoku.isCompleted.value!!)
            sudoku.createBackup()
    }
}