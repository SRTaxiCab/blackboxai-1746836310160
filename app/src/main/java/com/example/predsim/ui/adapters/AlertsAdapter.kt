package com.example.predsim.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.predsim.data.model.Alert
import com.example.predsim.databinding.ItemAlertBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertsAdapter(
    private val onAlertClicked: (Alert) -> Unit
) : ListAdapter<Alert, AlertsAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlertViewHolder(binding, onAlertClicked)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlertViewHolder(
        private val binding: ItemAlertBinding,
        private val onAlertClicked: (Alert) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: Alert) {
            with(binding) {
                // Set alert details
                tvAlertTitle.text = alert.title
                tvAlertDescription.text = alert.description
                tvTimestamp.text = getFormattedTimestamp(alert.timestamp)

                // Set severity indicator color
                severityIndicator.setBackgroundColor(alert.getSeverityColor())

                // Set region and source chips
                chipRegion.text = alert.region
                chipSource.text = alert.source

                // Set click listener
                root.setOnClickListener { onAlertClicked(alert) }
            }
        }

        private fun getFormattedTimestamp(date: Date): String {
            val now = System.currentTimeMillis()
            val timeDiff = now - date.time

            return when {
                timeDiff < HOUR_MILLIS -> {
                    val minutes = (timeDiff / MINUTE_MILLIS).toInt()
                    "${minutes}m ago"
                }
                timeDiff < DAY_MILLIS -> {
                    val hours = (timeDiff / HOUR_MILLIS).toInt()
                    "${hours}h ago"
                }
                timeDiff < WEEK_MILLIS -> {
                    val days = (timeDiff / DAY_MILLIS).toInt()
                    "${days}d ago"
                }
                else -> {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                }
            }
        }

        companion object {
            private const val MINUTE_MILLIS = 60_000L
            private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
            private const val DAY_MILLIS = 24 * HOUR_MILLIS
            private const val WEEK_MILLIS = 7 * DAY_MILLIS
        }
    }

    private class AlertDiffCallback : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }
    }
}
