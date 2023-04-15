package com.example.sudoku.data.backup

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class BackupHistory @OptIn(ExperimentalSerializationApi::class) constructor(
    @ProtoNumber(1) var x: Int = 0,
    @ProtoNumber(2) var y: Int = 0,
    @ProtoNumber(3) var numb: String = "0",
    @ProtoNumber(4) var notes: List<Int> = emptyList()
) {
    constructor(historyEntry: Triple<Pair<Int, Int>, String?, List<Int>>) : this (
        x = historyEntry.first.first,
        y = historyEntry.first.second,
        numb = historyEntry.second ?: "0",
        notes = historyEntry.third
            )

    fun getHistory(): Triple<Pair<Int, Int>, String?, List<Int>> {
        return Triple(Pair(x, y), if(numb == "0") null else numb, notes)
    }
}
