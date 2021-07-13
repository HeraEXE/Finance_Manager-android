package com.hera.inancemanager.ui.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hera.inancemanager.R
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.databinding.FragmentHistoryBinding
import com.hera.inancemanager.util.Constants
import com.hera.inancemanager.util.Constants.BY_AMOUNT
import com.hera.inancemanager.util.Constants.BY_AMOUNT_DESC
import com.hera.inancemanager.util.Constants.BY_DATE
import com.hera.inancemanager.util.Constants.BY_DATE_DESC
import com.hera.inancemanager.util.Constants.KEY_ORDER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history), HistoryAdapter.Listener {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var sharedPrefs: SharedPreferences
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
        sharedPrefs = (activity as AppCompatActivity).getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        historyAdapter = HistoryAdapter(requireContext(), this)
        _binding = FragmentHistoryBinding.bind(view)
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        lifecycleScope.launch {
            viewModel.entries.collect { entries ->
                historyAdapter.differ.submitList(entries)
            }
        }
        viewModel.type.value = args.type
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_date -> {
            viewModel.order.value = BY_DATE
            saveOrder(BY_DATE)
            true
        }
        R.id.action_date_desc -> {
            viewModel.order.value = BY_DATE_DESC
            saveOrder(BY_DATE_DESC)
            true
        }
        R.id.action_amount -> {
            viewModel.order.value = BY_AMOUNT
            saveOrder(BY_AMOUNT)
            true
        }
        R.id.action_amount_desc -> {
            viewModel.order.value = BY_AMOUNT_DESC
            saveOrder(BY_AMOUNT_DESC)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun saveOrder(order: Int) {
        sharedPrefs.edit().apply {
            putInt(KEY_ORDER, order)
            apply()
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