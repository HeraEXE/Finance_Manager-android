package com.hera.inancemanager.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hera.inancemanager.R
import com.hera.inancemanager.data.models.Entry
import com.hera.inancemanager.databinding.ItemEntryBinding
import com.hera.inancemanager.util.Constants.INCOME
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
        private val context: Context,
        private val listener: Listener
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


    interface Listener {

        fun onEntryClick(entry: Entry)
    }


    inner class ViewHolder(private val binding: ItemEntryBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(position: Int) {
                val entry = differ.currentList[position]
                binding.apply {
                    Picasso.get()
                            .load(icons[entry.type])
                            .placeholder(icons[entry.type])
                            .into(ivEntry)
                    ivEntry.setColorFilter(context.resources.getColor(colors[entry.type]))
                    tvAmount.text = entry.amount.toString()
                    tvAmount.setTextColor(context.resources.getColor(colors[entry.type]))
                    tvDateEntry.text = getDateFormatted(entry.date)
                    tvNoteEntry.text = entry.note
                    clItemEntry.setOnClickListener {
                        listener.onEntryClick(entry)
                    }
                }
            }


        private fun getDateFormatted(date: Long): String {
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            return formatter.format(cal.time)
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Entry, newItem: Entry) = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)


    override fun getItemCount() = differ.currentList.size


    companion object {
        private val icons = listOf(
                R.drawable.ic_up, R.drawable.ic_down
        )
        private val colors = listOf(
                R.color.whitish_green, R.color.whitish_red
        )
    }
}