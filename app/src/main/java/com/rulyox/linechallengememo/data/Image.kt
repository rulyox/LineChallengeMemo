package com.rulyox.linechallengememo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "image",
foreignKeys = [ForeignKey(entity = Memo::class, parentColumns = ["id"], childColumns = ["memo"], onDelete = CASCADE)])
data class Image(

    @PrimaryKey(autoGenerate = true) val id: Int?,
    val memo: Int,
    val file: String

)
