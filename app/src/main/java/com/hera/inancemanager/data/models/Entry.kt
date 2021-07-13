package com.hera.inancemanager.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "entry_table")
data class Entry(
    var amount: Double = 0.00,
    var type: Int = 0,
    var note: String = "",
    var date: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
) : Parcelable
