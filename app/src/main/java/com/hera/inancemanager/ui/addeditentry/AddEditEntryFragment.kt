package com.hera.inancemanager.ui.addeditentry

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hera.inancemanager.R
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.databinding.FragmentAddEditEntryBinding
import com.hera.inancemanager.util.Constants
import com.hera.inancemanager.util.Constants.DEFAULT_VALUE
import com.hera.inancemanager.util.Constants.EXPENSE
import com.hera.inancemanager.util.Constants.INCOME
import com.hera.inancemanager.util.Constants.KEY_EXPENSE
import com.hera.inancemanager.util.Constants.KEY_INCOME
import com.hera.inancemanager.util.Constants.KEY_TOTAL
import com.hera.inancemanager.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddEditEntryFragment : Fragment(R.layout.fragment_add_edit_entry), DatePickerDialog.OnDateSetListener {

    private val viewModel: AddEditEntryViewModel by viewModels()
    private lateinit var sharedPrefs: SharedPreferences
    private val args: AddEditEntryFragmentArgs by navArgs()
    private var addEdit = 0
    private lateinit var entry: Entry
    private var oldAmount: Double = 0.00
    private var _binding: FragmentAddEditEntryBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (args.entry != null) {
            addEdit = 1
            entry = args.entry!!
            oldAmount = entry.amount
        } else {
            entry = Entry()
        }
        (activity as AppCompatActivity).supportActionBar?.title = titles[addEdit]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs = (activity as AppCompatActivity).getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        _binding = FragmentAddEditEntryBinding.bind(view)
        showDate()
        binding.apply {
            if (addEdit == 1) {
                rgType.check(
                        when (entry.type) {
                            INCOME -> rbIncome.id
                            EXPENSE -> rbExpense.id
                            else -> -1
                        }
                )
                etAmount.setText(entry.amount.toString())
                etNote.setText(entry.note)
            }
            llDate.setOnClickListener {
                showDatePickerDialog(this@AddEditEntryFragment)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(menus[addEdit], menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_new -> {
            binding.apply {
                val amount = etAmount.text.toString()
                val note = etNote.text.toString()
                val type = when (rgType.checkedRadioButtonId) {
                    rbIncome.id -> INCOME
                    rbExpense.id -> EXPENSE
                    else -> INCOME
                }
                if (validate(amount, type)) {
                    (activity as AppCompatActivity).hideKeyboard()
                    val dialogLayout = layoutInflater.inflate(R.layout.alert_dialog, null)
                    val dialog = AlertDialog
                            .Builder(requireContext())
                            .setView(dialogLayout)
                            .create()
                    val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
                    val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
                    val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
                    val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
                    title.text = "New entry"
                    message.text = "Are you sure you want to add new entry?"
                    negativeBtn.text = "Cancel"
                    positiveBtn.text = "Add"
                    positiveBtn.setOnClickListener {
                        entry = entry.copy(amount = amount.toDouble(), type = type, note = note)
                        viewModel.insert(entry)
                        sharedPrefs.edit().apply {
                            val total: Double = if (entry.type == INCOME) {
                                sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() + amount.toDouble()
                            } else {
                                sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() - amount.toDouble()
                            }
                            val incomeExpense = sharedPrefs.getString(keys[entry.type], DEFAULT_VALUE)!!.toDouble() + amount.toDouble()
                            putString(KEY_TOTAL, total.toString())
                            putString(keys[entry.type], incomeExpense.toString())
                            apply()
                        }
                        dialog.dismiss()
                        val action = AddEditEntryFragmentDirections.actionAddEditEntryFragmentToHistoryFragment()
                        findNavController().navigate(action)
                    }
                    negativeBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
            true
        }
        R.id.action_save -> {
            binding.apply {
                val amount = etAmount.text.toString()
                val note = etNote.text.toString()
                val type = when (rgType.checkedRadioButtonId) {
                    rbIncome.id -> INCOME
                    rbExpense.id -> EXPENSE
                    else -> INCOME
                }
                if (validate(amount, type)) {
                    (activity as AppCompatActivity).hideKeyboard()
                    val dialogLayout = layoutInflater.inflate(R.layout.alert_dialog, null)
                    val dialog = AlertDialog
                            .Builder(requireContext())
                            .setView(dialogLayout)
                            .create()
                    val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
                    val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
                    val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
                    val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
                    title.text = "Edit entry"
                    message.text = "Are you sure you want to edit this entry?"
                    negativeBtn.text = "Cancel"
                    positiveBtn.text = "Edit"
                    positiveBtn.setOnClickListener {
                        entry = entry.copy(amount = amount.toDouble(), type = type, note = note)
                        viewModel.update(entry)
                        sharedPrefs.edit().apply {
                            val total = if (entry.type == INCOME) {
                                sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() - oldAmount + amount.toDouble()
                            } else {
                                sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() + oldAmount - amount.toDouble()
                            }
                            val incomeExpense = sharedPrefs.getString(keys[entry.type], DEFAULT_VALUE)!!.toDouble() - oldAmount + amount.toDouble()
                            putString(KEY_TOTAL, total.toString())
                            putString(keys[entry.type], incomeExpense.toString())
                            apply()
                        }
                        dialog.dismiss()
                        val action = AddEditEntryFragmentDirections.actionAddEditEntryFragmentToHistoryFragment()
                        findNavController().navigate(action)
                    }
                    negativeBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
            true
        }
        R.id.action_delete -> {
            (activity as AppCompatActivity).hideKeyboard()
            val dialogLayout = layoutInflater.inflate(R.layout.alert_dialog, null)
            val dialog = AlertDialog
                    .Builder(requireContext())
                    .setView(dialogLayout)
                    .create()
            val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
            val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
            val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
            val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
            title.text = "Delete entry"
            message.text = "Are you sure you want to delete this entry?"
            negativeBtn.text = "Cancel"
            positiveBtn.text = "Delete"
            positiveBtn.setOnClickListener {
                viewModel.delete(entry)
                sharedPrefs.edit().apply {
                    val total = if (entry.type == INCOME) {
                        sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() - oldAmount
                    } else {
                        sharedPrefs.getString(KEY_TOTAL, DEFAULT_VALUE)!!.toDouble() + oldAmount
                    }
                    val incomeExpense = sharedPrefs.getString(keys[entry.type], DEFAULT_VALUE)!!.toDouble() - oldAmount
                    putString(KEY_TOTAL, total.toString())
                    putString(keys[entry.type], incomeExpense.toString())
                    apply()
                }
                dialog.dismiss()
                val action = AddEditEntryFragmentDirections.actionAddEditEntryFragmentToHistoryFragment()
                findNavController().navigate(action)
            }
            negativeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun validate(amount: String, type: Int): Boolean {
        var isValid = true
        when {
            amount.isEmpty() -> {
                isValid = false
                binding.etAmount.error = "Empty"
            }
            else -> {
                binding.etAmount.error = null
            }
        }
        if (type == -1) {
            isValid = false
            Toast.makeText(requireContext(), "Choose a type", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }


    private fun showDate() {
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        val cal = Calendar.getInstance()
        cal.timeInMillis = entry.date
        binding.tvDate.text = formatter.format(cal.time)
    }


    private fun showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(
                requireContext(),
                onDateSetListener,
                year, month, day
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        val cal = Calendar.getInstance()
        cal.set(year, month, dayOfMonth)
        entry.date = cal.timeInMillis
        binding.tvDate.text = formatter.format(cal.time)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        val titles = listOf("Add", "Edit")
        val menus = listOf(R.menu.menu_add_entry, R.menu.menu_edit_entry)
        val keys = listOf(KEY_INCOME, KEY_EXPENSE)
    }
}