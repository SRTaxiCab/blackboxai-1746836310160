package com.example.predsim.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.predsim.data.model.Alert
import com.example.predsim.data.model.Prediction
import com.example.predsim.data.repository.PredictiveRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val repository = PredictiveRepository.getInstance()

    // Risk Level
    private val _currentRiskLevel = MutableLiveData<Prediction.RiskLevel>()
    val currentRiskLevel: LiveData<Prediction.RiskLevel> = _currentRiskLevel

    // Predictions
    private val _predictions = MutableLiveData<List<Prediction>>()
    val predictions: LiveData<List<Prediction>> = _predictions

    // Alerts
    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> = _alerts

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Filter states
    private var currentRegion: String? = null
    private var currentTopic: String? = null
    private var currentSource: String? = null

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load risk level
                repository.getCurrentRiskLevel()
                    .catch { e ->
                        _error.value = "Failed to load risk level: ${e.message}"
                    }
                    .collect { riskLevel ->
                        _currentRiskLevel.value = riskLevel
                    }

                // Load predictions
                repository.getPredictions(currentRegion, currentTopic, currentSource)
                    .catch { e ->
                        _error.value = "Failed to load predictions: ${e.message}"
                    }
                    .collect { predictionsList ->
                        _predictions.value = predictionsList
                    }

                // Load alerts
                repository.getAlerts()
                    .catch { e ->
                        _error.value = "Failed to load alerts: ${e.message}"
                    }
                    .collect { alertsList ->
                        _alerts.value = alertsList
                    }

            } catch (e: Exception) {
                _error.value = "Error loading dashboard data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyFilters(region: String? = null, topic: String? = null, source: String? = null) {
        currentRegion = region
        currentTopic = topic
        currentSource = source
        loadDashboardData()
    }

    fun clearFilters() {
        currentRegion = null
        currentTopic = null
        currentSource = null
        loadDashboardData()
    }

    fun exportReport() {
        viewModelScope.launch {
            // TODO: Implement report generation and export
            // This would typically:
            // 1. Generate a PDF/Excel report
            // 2. Save it to external storage
            // 3. Share it via intent
        }
    }

    fun getRiskLevelColor(riskLevel: Prediction.RiskLevel): Int {
        return when (riskLevel) {
            Prediction.RiskLevel.LOW -> android.graphics.Color.parseColor("#4CAF50")
            Prediction.RiskLevel.MEDIUM -> android.graphics.Color.parseColor("#FFC107")
            Prediction.RiskLevel.HIGH -> android.graphics.Color.parseColor("#FF5722")
            Prediction.RiskLevel.CRITICAL -> android.graphics.Color.parseColor("#F44336")
        }
    }

    fun getFilterOptions(): Triple<List<String>, List<String>, List<String>> {
        return Triple(
            listOf("North America", "Europe", "Asia", "Africa", "South America"),
            listOf("Economic", "Political", "Social", "Environmental", "Technological"),
            listOf("News Media", "Social Media", "Government Data", "Satellite Data")
        )
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}
