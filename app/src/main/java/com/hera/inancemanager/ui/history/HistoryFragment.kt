package com.hera.inancemanager.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hera.inancemanager.R
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.databinding.FragmentHistoryBinding
import com.hera.inancemanager.util.Constants.BY_AMOUNT
import com.hera.inancemanager.util.Constants.BY_AMOUNT_DESC
import com.hera.inancemanager.util.Constants.BY_DATE
import com.hera.inancemanager.util.Constants.BY_DATE_DESC
import com.hera.inancemanager.util.Constants.KEY_ORDER
import com.hera.inancemanager.util.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history), HistoryAdapter.Listener {

    private val viewModel: HistoryViewModel by viewModels()
    private val args: HistoryFragmentArgs by navArgs()
    private lateinit var historyAdapter: HistoryAdapter
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "History"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAdapter = HistoryAdapter(requireContext(), this)
        _binding = FragmentHistoryBinding.bind(view)
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        val orderKey = intPreferencesKey(KEY_ORDER)
        val orderFlow: Flow<Int> = requireContext().dataStore.data.map { preferences ->
            preferences[orderKey] ?: 0
        }
        lifecycleScope.launch {
            orderFlow.collect {
                viewModel.order.value = it
            }
        }
        viewModel.type.value = args.type
        lifecycleScope.launch {
            viewModel.entries.collect { entries ->
                historyAdapter.differ.submitList(entries)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_date -> {
            viewModel.order.value = BY_DATE
            lifecycleScope.launch {
                saveOrder(BY_DATE)
            }
            true
        }
        R.id.action_date_desc -> {
            viewModel.order.value = BY_DATE_DESC
            lifecycleScope.launch {
                saveOrder(BY_DATE_DESC)
            }
            true
        }
        R.id.action_amount -> {
            viewModel.order.value = BY_AMOUNT
            lifecycleScope.launch {
                saveOrder(BY_AMOUNT)
            }
            true
        }
        R.id.action_amount_desc -> {
            viewModel.order.value = BY_AMOUNT_DESC
            lifecycleScope.launch {
                saveOrder(BY_AMOUNT_DESC)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private suspend fun saveOrder(order: Int) {
        val orderKey = intPreferencesKey(KEY_ORDER)
        requireContext().dataStore.edit {  settings ->
            settings[orderKey] = order
        }
    }


    override fun onEntryClick(entry: Entry) {
        val action = HistoryFragmentDirections.actionHistoryFragmentToAddEditEntryFragment(entry)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}