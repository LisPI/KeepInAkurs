package com.develop.room530.lis.akursnotify.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.develop.room530.lis.akursnotify.data.database.NbrbHistory
import com.develop.room530.lis.akursnotify.databinding.GoalCardBinding
import com.develop.room530.lis.akursnotify.getDateDDMMYYFormat

class HistoryRatesAdapter : ListAdapter<NbrbHistory, HistoryRatesAdapter.ViewHolder>(
    RatesHistoryDiffCallback()
) { // FIXME change RateModel to UIRate
    class ViewHolder(private val binding: GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NbrbHistory) {
            with(binding) {
                rate.text = item.rate.format(4)
                rateLabel.text = "Нацбанк"
                rateTrend.text = "от ${getDateDDMMYYFormat(item.date)}"//getDateDDMMFormat(item.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GoalCardBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RatesHistoryDiffCallback : DiffUtil.ItemCallback<NbrbHistory>() {
    override fun areItemsTheSame(oldItem: NbrbHistory, newItem: NbrbHistory) =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: NbrbHistory, newItem: NbrbHistory) =
        oldItem == newItem
}