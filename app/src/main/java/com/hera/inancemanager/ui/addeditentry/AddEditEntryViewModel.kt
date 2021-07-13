package com.hera.inancemanager.ui.addeditentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hera.inancemanager.data.EntryDao
import com.hera.inancemanager.data.models.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditEntryViewModel @Inject constructor(
        private val db: EntryDao
) : ViewModel() {

    fun insert(entry: Entry) = viewModelScope.launch {
        db.insert(entry)
    }


    fun update(entry: Entry) = viewModelScope.launch {
        db.update(entry)
    }


    fun delete(entry: Entry) = viewModelScope.launch {
        db.delete(entry)
    }
}