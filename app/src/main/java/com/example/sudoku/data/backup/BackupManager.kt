package com.example.sudoku.data.backup

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class BackupManager(
    private val context: Context,
    private val backupFileName: String = "Sudoku_Backup.proto"
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun createBackup(backup: BackupGame) {
        context.openFileOutput(backupFileName, Context.MODE_PRIVATE).use {
            it.write(ProtoBuf.encodeToByteArray(backup))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun restoreBackup(): BackupGame {
        context.openFileInput(backupFileName).use {
            return ProtoBuf.decodeFromByteArray(it.readBytes())
        }
    }
}