package com.hera.inancemanager.data

import androidx.room.*
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.util.Constants.BY_AMOUNT
import com.hera.inancemanager.util.Constants.BY_AMOUNT_DESC
import com.hera.inancemanager.util.Constants.BY_DATE
import com.hera.inancemanager.util.Constants.BY_DATE_DESC
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)


    @Update
    suspend fun update(entry: Entry)


    @Delete
    suspend fun delete(entry: Entry)


    @Query("SELECT * FROM entry_table WHERE CASE WHEN :type = 2 THEN type = 0 OR type = 1 ELSE type = :type END ORDER BY date")
    fun getEntriesByDate(type: Int): Flow<List<Entry>>

    @Query("SELECT * FROM entry_table WHERE CASE WHEN :type = 2 THEN type = 0 OR type = 1 ELSE type = :type END ORDER BY date DESC")
    fun getEntriesByDateDesc(type: Int): Flow<List<Entry>>

    @Query("SELECT * FROM entry_table WHERE CASE WHEN :type = 2 THEN type = 0 OR type = 1 ELSE type = :type END ORDER BY amount")
    fun getEntriesByAmount(type: Int): Flow<List<Entry>>

    @Query("SELECT * FROM entry_table WHERE CASE WHEN :type = 2 THEN type = 0 OR type = 1 ELSE type = :type END ORDER BY amount DESC")
    fun getEntriesByAmountDesc(type: Int): Flow<List<Entry>>

    fun getEntries(order: Int, type: Int) = when (order) {
        BY_DATE -> getEntriesByDate(type)
        BY_DATE_DESC -> getEntriesByDateDesc(type)
        BY_AMOUNT -> getEntriesByAmount(type)
        BY_AMOUNT_DESC -> getEntriesByAmountDesc(type)
        else -> getEntriesByDate(type)

    }
}