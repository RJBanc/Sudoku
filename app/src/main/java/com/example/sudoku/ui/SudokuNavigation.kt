package com.example.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sudoku.viewmodel.SudokuViewModel

enum class SudokuScreen {
    Game,
    Settings
}

@Composable
fun SudokuNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sudokuViewModel: SudokuViewModel
) {
    NavHost(
        navController = navController,
        startDestination = SudokuScreen.Game.name,
        modifier = modifier
    ) {
        composable(route = SudokuScreen.Game.name) {
            GameScreen(
                modifier = modifier,
                sudokuGame = sudokuViewModel,
                onSettingsClicked = {
                    sudokuViewModel.pauseGame()
                    navController.navigate(SudokuScreen.Settings.name)
                }
            )
        }
        composable(route = SudokuScreen.Settings.name) {
            SettingsScreen(
                modifier = modifier,
                onBackClicked = {
                    navController.navigate(SudokuScreen.Game.name)
                }
            )
        }
    }
}