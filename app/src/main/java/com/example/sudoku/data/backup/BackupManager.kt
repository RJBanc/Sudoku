package com.example.sudoku.data.backup

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

class BackupManager(
    private val context: Context,
    private val backupFileName: String = "Sudoku_Backup.proto"
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createBackup(backup: BackupGame) {
        withContext(Dispatchers.IO) {
            context.openFileOutput(backupFileName, Context.MODE_PRIVATE).use {
                it.write(ProtoBuf.encodeToByteArray(backup))
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun restoreBackup(): BackupGame {
        return withContext(Dispatchers.IO) {
            context.openFileInput(backupFileName).use {
                ProtoBuf.decodeFromByteArray(it.readBytes())
            }
        }
    }
}