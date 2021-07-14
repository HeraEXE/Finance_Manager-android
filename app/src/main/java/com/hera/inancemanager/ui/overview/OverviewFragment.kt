package com.hera.inancemanager.ui.overview

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.fragment.findNavController
import com.hera.inancemanager.R
import com.hera.inancemanager.databinding.FragmentOverviewBinding
import com.hera.inancemanager.util.Constants.ALL
import com.hera.inancemanager.util.Constants.DEFAULT_VALUE
import com.hera.inancemanager.util.Constants.KEY_EXPENSE
import com.hera.inancemanager.util.Constants.KEY_INCOME
import com.hera.inancemanager.util.Constants.KEY_TOTAL
import com.hera.inancemanager.util.Constants.SHARED_PREFS_NAME
import com.hera.inancemanager.util.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private lateinit var sharedPrefs: SharedPreferences
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Overview"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs = (activity as AppCompatActivity).getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        _binding = FragmentOverviewBinding.bind(view)
        binding.apply {
            tvTotal.text = sharedPrefs.getInt(KEY_TOTAL, DEFAULT_VALUE).toString()
            tvIncome.text = sharedPrefs.getInt(KEY_INCOME, DEFAULT_VALUE).toString()
            tvExpense.text = sharedPrefs.getInt(KEY_EXPENSE, DEFAULT_VALUE).toString()
            val titles = listOf(
                tvIncomeTitle, tvExpenseTitle, tvTotalTitle
            )
            for (i in titles.indices) {
                titles[i].setOnClickListener {
                    val action = OverviewFragmentDirections.actionOverviewFragmentToHistoryFragment(i)
                    findNavController().navigate(action)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}