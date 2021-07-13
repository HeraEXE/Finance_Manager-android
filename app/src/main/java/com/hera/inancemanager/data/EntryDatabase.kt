package com.hera.inancemanager.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hera.inancemanager.data.models.Entry

@Database(entities = [Entry::class], version = 1)
abstract class EntryDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
}