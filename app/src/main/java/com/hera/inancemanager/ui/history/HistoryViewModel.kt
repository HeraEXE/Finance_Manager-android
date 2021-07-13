package com.hera.inancemanager.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hera.inancemanager.data.EntryDao
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.util.Constants.ALL
import com.hera.inancemanager.util.Constants.BY_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val db: EntryDao
) : ViewModel() {

    val order = MutableStateFlow(BY_DATE)
    val type = MutableStateFlow(ALL)
    val entries = combine(order, type) { order, type ->
        Pair(order, type)
    }.flatMapLatest { (order, type) ->
        db.getEntries(order, type)
    }
}