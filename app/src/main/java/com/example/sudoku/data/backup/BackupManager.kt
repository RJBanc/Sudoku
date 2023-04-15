package com.example.sudoku.data.backup

import android.content.Context
import com.example.sudoku.SudokuLogic
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class BackupManager(
    private val context: Context,
    private val sudoku: SudokuLogic,
    private val backupFileName: String = "Sudoku_Backup.proto"
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun createBackup() {
        context.openFileOutput(backupFileName, Context.MODE_PRIVATE).use {
            it.write(ProtoBuf.encodeToByteArray(sudoku.createBackup()))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun restoreBackup() {
        context.openFileInput(backupFileName).use {
            sudoku.restoreBackup(ProtoBuf.decodeFromByteArray(it.readBytes()))
        }
    }
}