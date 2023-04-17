package com.example.sudoku.data.backup

import com.example.sudoku.Difficulty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class BackupGame @OptIn(ExperimentalSerializationApi::class) constructor(
    @ProtoNumber(1) var difficulty: Difficulty = Difficulty.EASY,
    @ProtoNumber(2) var solution: String = "",
    @ProtoNumber(3) var puzzle: String = "",
    @ProtoNumber(4) var editable: List<Boolean> = emptyList(),
    @ProtoNumber(5) var notes: List<Int> = emptyList(),
    @ProtoNumber(6) var history: List<BackupHistory> = emptyList(),
    @ProtoNumber(7) var timeElapse: Long = 0L
)