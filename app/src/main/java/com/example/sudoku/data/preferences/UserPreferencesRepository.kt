package com.example.sudoku.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.prefs.InvalidPreferencesFormatException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val keepScreenOn: Flow<Boolean> = dataStore.data.catch {
        if(it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { it[KEEP_SCREEN_ON] ?: false }

    val createInitialNotes: Flow<Boolean> = dataStore.data.catch {
        if(it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { it[CREATE_INITIAL_NOTES] ?: false }

    val confirmNewGame: Flow<Boolean> = dataStore.data.catch {
        if(it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { it[CONFIRM_BEFORE_NEW_GAME] ?: true }

    val nightMode: Flow<String> = dataStore.data.catch {
        if(it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { it[NIGHT_MODE] ?: "system" }


    private companion object {
        val nightModeOptions = listOf("on", "off", "system")

        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val CREATE_INITIAL_NOTES = booleanPreferencesKey("create_initial_notes")
        val CONFIRM_BEFORE_NEW_GAME = booleanPreferencesKey("confirm_before_new_game")
        val NIGHT_MODE = stringPreferencesKey("night_mode")

        const val TAG = "UserPreferencesRepo"
    }


    suspend fun saveScreenOnPreference(keepScreenOn: Boolean) {
        dataStore
        try {
            dataStore.edit {
                it[KEEP_SCREEN_ON] = keepScreenOn
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing preferences.")
        }
    }

    suspend fun saveInitialNotesPreference(initialNotes: Boolean) {
        try {
            dataStore.edit {
                it[CREATE_INITIAL_NOTES] = initialNotes
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing preferences.")
        }
    }

    suspend fun saveNewGameConfirm(confirmNewGame: Boolean) {
        try {
            dataStore.edit {
                it[CONFIRM_BEFORE_NEW_GAME] = confirmNewGame
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing preferences.")
        }
    }

    suspend fun saveNightMode(nightMode: String) {
        if (nightMode !in nightModeOptions)
            throw InvalidPreferencesFormatException("'$nightMode' is not a valid option")

        try {
            dataStore.edit {
                it[NIGHT_MODE] = nightMode
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing preferences.")
        }
    }
}