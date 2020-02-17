package com.rulyox.linechallengememo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(

    @PrimaryKey(autoGenerate = true) val id: Int?,
    val title: String,
    val text: String,
    val time: Long

)
