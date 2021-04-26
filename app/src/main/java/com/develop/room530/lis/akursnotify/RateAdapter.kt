package com.develop.room530.lis.akursnotify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.develop.room530.lis.akursnotify.databinding.HolderRateBinding
import com.develop.room530.lis.akursnotify.model.AlfaRateModel
import com.develop.room530.lis.akursnotify.model.NbRbRateModel
import com.develop.room530.lis.akursnotify.model.RateModel

class RateAdapter() : ListAdapter<RateModel, RateAdapter.ViewHolder>(RatesDiffCallback()) { // FIXME change RateModel to UIRate
    class ViewHolder(private val binding: HolderRateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RateModel) {
            with(binding) {
                rate.text = item.rate.format(4)
                rateLabel.text = when (item) {
                    is AlfaRateModel -> "А-Курс"
                    is NbRbRateModel -> "Нацбанк"
                } + " от ${item.dateUI}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = HolderRateBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RatesDiffCallback : DiffUtil.ItemCallback<RateModel>() {
    override fun areItemsTheSame(oldItem: RateModel, newItem: RateModel) =
        oldItem.date == newItem.date

    override fun areContentsTheSame(oldItem: RateModel, newItem: RateModel) =
        oldItem == newItem
}
