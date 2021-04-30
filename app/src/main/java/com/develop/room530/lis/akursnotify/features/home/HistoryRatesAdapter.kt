package com.develop.room530.lis.akursnotify.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.develop.room530.lis.akursnotify.R
import com.develop.room530.lis.akursnotify.data.database.NbrbHistory
import com.develop.room530.lis.akursnotify.databinding.GoalCardBinding
import com.develop.room530.lis.akursnotify.getDateDDMMYYFormat

class HistoryRatesAdapter(private val onClick: (item: NbrbHistory) -> Unit) : ListAdapter<NbrbHistory, HistoryRatesAdapter.ViewHolder>(
    RatesHistoryDiffCallback()
) { // FIXME change RateModel to UIRate
    class ViewHolder(private val binding: GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NbrbHistory) {
            with(binding) {
                rate.text = item.rate.format(4)
                rateLabel.text = rateLabel.context.getString(R.string.NB)
                rateTrend.text = rateTrend.context.getString(R.string.rates_by_date, getDateDDMMYYFormat(item.date))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GoalCardBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding).apply {
            itemView.setOnClickListener { onClick.invoke(getItem(adapterPosition)) }
        }
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