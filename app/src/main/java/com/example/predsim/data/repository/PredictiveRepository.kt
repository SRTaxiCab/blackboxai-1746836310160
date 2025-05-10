package com.example.predsim.data.repository

import com.example.predsim.data.model.Alert
import com.example.predsim.data.model.Prediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class PredictiveRepository {
    
    // In a real app, these would come from a remote data source and local database
    private val regions = listOf("North America", "Europe", "Asia", "Africa", "South America")
    private val topics = listOf("Economic", "Political", "Social", "Environmental", "Technological")
    private val sources = listOf("News Media", "Social Media", "Government Data", "Satellite Data")

    fun getPredictions(
        region: String? = null,
        topic: String? = null,
        source: String? = null
    ): Flow<List<Prediction>> = flow {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)

        // Generate sample predictions
        val predictions = (1..10).map { createSamplePrediction() }
            .filter { prediction ->
                (region == null || prediction.region == region) &&
                (topic == null || prediction.topic == topic) &&
                (source == null || prediction.source == source)
            }

        emit(predictions)
    }.flowOn(Dispatchers.IO)

    fun getAlerts(
        severity: Alert.Severity? = null,
        region: String? = null
    ): Flow<List<Alert>> = flow {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)

        // Generate sample alerts
        val alerts = (1..5).map { createSampleAlert() }
            .filter { alert ->
                (severity == null || alert.severity == severity) &&
                (region == null || alert.region == region)
            }

        emit(alerts)
    }.flowOn(Dispatchers.IO)

    fun getCurrentRiskLevel(): Flow<Prediction.RiskLevel> = flow {
        // Simulate network delay
        kotlinx.coroutines.delay(500)

        // Generate sample risk level
        val riskScore = Random.nextInt(0, 100)
        emit(Prediction.RiskLevel.fromScore(riskScore))
    }.flowOn(Dispatchers.IO)

    private fun createSamplePrediction(): Prediction {
        val riskScore = Random.nextFloat() * 100
        val dataPoints = generateSampleDataPoints()

        return Prediction(
            id = UUID.randomUUID().toString(),
            timestamp = Date(),
            riskLevel = Prediction.RiskLevel.fromScore(riskScore.toInt()),
            riskScore = riskScore,
            confidence = Random.nextFloat(),
            region = regions.random(),
            topic = topics.random(),
            source = sources.random(),
            dataPoints = dataPoints,
            insights = generateSampleInsights()
        )
    }

    private fun createSampleAlert(): Alert {
        return Alert(
            id = UUID.randomUUID().toString(),
            title = "Alert ${Random.nextInt(1000)}",
            description = "Sample alert description for testing purposes",
            severity = Alert.Severity.values().random(),
            timestamp = Date(),
            source = sources.random(),
            region = regions.random()
        )
    }

    private fun generateSampleDataPoints(): List<Prediction.DataPoint> {
        val points = mutableListOf<Prediction.DataPoint>()
        val baseValue = Random.nextFloat() * 50

        // Generate historical data points
        for (i in -10..0) {
            points.add(
                Prediction.DataPoint(
                    timestamp = Date(System.currentTimeMillis() + (i * 86400000)), // i days
                    value = baseValue + Random.nextFloat() * 10,
                    label = "Historical Point $i",
                    isActual = true
                )
            )
        }

        // Generate prediction points
        for (i in 1..10) {
            points.add(
                Prediction.DataPoint(
                    timestamp = Date(System.currentTimeMillis() + (i * 86400000)), // i days
                    value = baseValue + Random.nextFloat() * 20,
                    label = "Prediction Point $i",
                    isActual = false
                )
            )
        }

        return points
    }

    private fun generateSampleInsights(): List<String> {
        return listOf(
            "Increasing trend detected in the last 7 days",
            "Correlation found with recent events",
            "Similar patterns observed in historical data",
            "Confidence level above threshold"
        ).shuffled().take(Random.nextInt(2, 4))
    }

    companion object {
        @Volatile
        private var instance: PredictiveRepository? = null

        fun getInstance(): PredictiveRepository {
            return instance ?: synchronized(this) {
                instance ?: PredictiveRepository().also { instance = it }
            }
        }
    }
}
