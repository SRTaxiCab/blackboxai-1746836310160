package com.example.predsim.data.model

import java.util.Date

data class Prediction(
    val id: String,
    val timestamp: Date,
    val riskLevel: RiskLevel,
    val riskScore: Float, // 0-100
    val confidence: Float, // 0-1
    val region: String,
    val topic: String,
    val source: String,
    val dataPoints: List<DataPoint>,
    val insights: List<String>
) {
    enum class RiskLevel(val score: IntRange) {
        LOW(0..25),
        MEDIUM(26..50),
        HIGH(51..75),
        CRITICAL(76..100);

        companion object {
            fun fromScore(score: Int): RiskLevel {
                return values().first { score in it.score }
            }
        }
    }

    data class DataPoint(
        val timestamp: Date,
        val value: Float,
        val label: String,
        val isActual: Boolean // true for historical data, false for predictions
    )

    fun getRiskColor(): Int {
        return when (riskLevel) {
            RiskLevel.LOW -> android.graphics.Color.parseColor("#4CAF50")     // Green
            RiskLevel.MEDIUM -> android.graphics.Color.parseColor("#FFC107")  // Yellow
            RiskLevel.HIGH -> android.graphics.Color.parseColor("#FF5722")    // Orange
            RiskLevel.CRITICAL -> android.graphics.Color.parseColor("#F44336")// Red
        }
    }

    fun getFormattedRiskScore(): String {
        return String.format("%.1f%%", riskScore)
    }

    fun getFormattedConfidence(): String {
        return String.format("%.1f%%", confidence * 100)
    }

    companion object {
        const val MAX_RISK_SCORE = 100f
        const val MIN_RISK_SCORE = 0f
        const val MAX_CONFIDENCE = 1f
        const val MIN_CONFIDENCE = 0f
    }
}
