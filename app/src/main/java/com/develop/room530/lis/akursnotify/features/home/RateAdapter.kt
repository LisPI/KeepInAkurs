package com.develop.room530.lis.akursnotify.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.develop.room530.lis.akursnotify.R
import com.develop.room530.lis.akursnotify.data.database.RatesGoal
import com.develop.room530.lis.akursnotify.databinding.GoalCardBinding

class RateAdapter(private val onClick: (item: RatesGoal) -> Unit) :
    ListAdapter<RatesGoal, RateAdapter.ViewHolder>(RatesDiffCallback()) { // FIXME change RateModel to UIRate
    class ViewHolder(private val binding: GoalCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RatesGoal) {
            with(binding) {
                rate.text = item.rate.format(4)
                rateLabel.text =
                    if (item.bank == binding.rateTrend.context.getString(
                            R.string.NB_non_locale
                        )
                    ) binding.rateTrend.context.getString(R.string.NB)
                    else binding.rateTrend.context.getString(R.string.Akurs)
                rateTrend.text =
                    if (item.trend < 0) binding.rateTrend.context.getString(R.string.cheap_label) else binding.rateTrend.context.getString(
                        R.string.expensive_label
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GoalCardBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) onClick.invoke(
                    getItem(
                        adapterPosition
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RatesDiffCallback : DiffUtil.ItemCallback<RatesGoal>() {
    override fun areItemsTheSame(oldItem: RatesGoal, newItem: RatesGoal) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RatesGoal, newItem: RatesGoal) =
        oldItem == newItem
}
