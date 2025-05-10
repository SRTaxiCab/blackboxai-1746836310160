package com.example.predsim.utils

import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import com.example.predsim.R
import com.example.predsim.data.model.Prediction
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChartFormatter {

    fun setupPredictionChart(chart: LineChart, context: Context) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            // Customize X-Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = DateAxisFormatter()
            }
            
            // Customize Left Y-Axis
            axisLeft.apply {
                textColor = Color.BLACK
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
                axisMinimum = 0f
                axisMaximum = 100f
            }
            
            // Disable Right Y-Axis
            axisRight.isEnabled = false
            
            // Animation
            animateX(1000)
        }
    }

    fun createPredictionDataSet(
        predictions: List<Prediction.DataPoint>,
        context: Context,
        isActual: Boolean
    ): LineDataSet {
        val entries = predictions
            .filter { it.isActual == isActual }
            .map { Entry(it.timestamp.time.toFloat(), it.value) }

        return LineDataSet(entries, if (isActual) "Historical" else "Predicted").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            
            // Line styling
            lineWidth = 2f
            color = if (isActual) {
                context.getColor(R.color.primary)
            } else {
                context.getColor(R.color.secondary)
            }
            
            // Circle styling
            setDrawCircles(true)
            setCircleColor(color)
            circleRadius = 4f
            
            // Value styling
            setDrawValues(false)
            
            // Highlight styling
            highLightColor = Color.RED
            
            if (!isActual) {
                // Add dashed line effect for predictions
                enableDashedLine(10f, 5f, 0f)
                setDrawCircles(false)
            }
            
            // Fill styling
            setDrawFilled(true)
            fillAlpha = 30
            fillColor = color
        }
    }

    fun updateChartWithPredictions(
        chart: LineChart,
        predictions: List<Prediction.DataPoint>,
        context: Context
    ) {
        // Create datasets for historical and predicted data
        val historicalDataSet = createPredictionDataSet(predictions, context, true)
        val predictedDataSet = createPredictionDataSet(predictions, context, false)
        
        // Combine datasets
        val lineData = LineData(historicalDataSet, predictedDataSet)
        
        // Update chart
        chart.data = lineData
        chart.invalidate()
    }

    private class DateAxisFormatter : ValueFormatter() {
        private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        
        override fun getFormattedValue(value: Float): String {
            return dateFormat.format(Date(value.toLong()))
        }
    }

    fun createRiskLevelGradient(riskLevel: Prediction.RiskLevel): IntArray {
        return when (riskLevel) {
            Prediction.RiskLevel.LOW -> intArrayOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#81C784")
            )
            Prediction.RiskLevel.MEDIUM -> intArrayOf(
                Color.parseColor("#FFC107"),
                Color.parseColor("#FFD54F")
            )
            Prediction.RiskLevel.HIGH -> intArrayOf(
                Color.parseColor("#FF5722"),
                Color.parseColor("#FF8A65")
            )
            Prediction.RiskLevel.CRITICAL -> intArrayOf(
                Color.parseColor("#F44336"),
                Color.parseColor("#E57373")
            )
        }
    }
}
