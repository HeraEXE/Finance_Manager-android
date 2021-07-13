package com.hera.inancemanager.di

import android.content.Context
import androidx.room.Room
import com.hera.inancemanager.data.EntryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEntryDatabase(
        @ApplicationContext context: Context
    ) = Room
        .databaseBuilder(
            context,
            EntryDatabase::class.java,
            "entry_database"
        )
        .build()


    @Provides
    fun provideEntryDao(
        entryDatabase: EntryDatabase
    ) = entryDatabase.entryDao()
}