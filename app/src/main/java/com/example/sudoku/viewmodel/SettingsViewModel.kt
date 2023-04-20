package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sudoku.SudokuApplication
import com.example.sudoku.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SudokuApplication)
                SettingsViewModel(application.userPreferencesRepository)
            }
        }
    }

    val screenOn: StateFlow<Boolean> = userPreferencesRepository.keepScreenOn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    val initialNotes: StateFlow<Boolean> = userPreferencesRepository.createInitialNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
    val confirmNewGame: StateFlow<Boolean> = userPreferencesRepository.confirmNewGame
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )
    val nightMode: StateFlow<String> = userPreferencesRepository.nightMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "system"
        )


    fun selectScreenOn(keepScreenOn: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveScreenOnPreference(keepScreenOn)
        }
    }

    fun selectInitialNotes(createNotes: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveInitialNotesPreference(createNotes)
        }
    }

    fun selectNewGameConfirm(confirm: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveNewGameConfirm(confirm)
        }
    }

    fun selectNightMode(mode: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveNightMode(mode)
        }
    }
}