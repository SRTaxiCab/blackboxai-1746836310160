package com.example.predsim.data.model

import java.util.Date

data class Alert(
    val id: String,
    val title: String,
    val description: String,
    val severity: Severity,
    val timestamp: Date,
    val source: String,
    val region: String,
    val isRead: Boolean = false
) {
    enum class Severity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    fun getSeverityColor(): Int {
        return when (severity) {
            Severity.LOW -> android.graphics.Color.parseColor("#4CAF50")     // Green
            Severity.MEDIUM -> android.graphics.Color.parseColor("#FFC107")  // Yellow
            Severity.HIGH -> android.graphics.Color.parseColor("#FF5722")    // Orange
            Severity.CRITICAL -> android.graphics.Color.parseColor("#F44336")// Red
        }
    }
}
