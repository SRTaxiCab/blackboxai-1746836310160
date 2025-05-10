package com.example.predsim.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.predsim.R
import com.example.predsim.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadDashboardData()
    }

    private fun setupUI() {
        setupRiskLevel()
        setupCharts()
        setupFilters()
        setupExportButton()
    }

    private fun setupRiskLevel() {
        with(binding) {
            // Set up risk level indicator
            riskLevelIndicator.apply {
                setIndicatorColor(resources.getColor(R.color.risk_medium, null))
                trackColor = resources.getColor(R.color.gray_200, null)
                progress = 65 // Sample value
            }
            tvRiskLevel.text = "Medium Risk Level"
        }
    }

    private fun setupCharts() {
        with(binding.lineChart) {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = Color.BLACK
                setDrawGridLines(true)
            }

            axisRight.isEnabled = false
            legend.isEnabled = true

            // Sample data
            val entries = ArrayList<Entry>()
            for (i in 0..10) {
                entries.add(Entry(i.toFloat(), (Math.random() * 80 + 20).toFloat()))
            }

            val dataSet = LineDataSet(entries, "Prediction Trend").apply {
                color = resources.getColor(R.color.primary, null)
                lineWidth = 2f
                setDrawCircles(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f
            }

            data = LineData(dataSet)
            invalidate()
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds[0])
                applyFilter(chip.text.toString())
            }
        }
    }

    private fun setupExportButton() {
        binding.fabExport.setOnClickListener {
            // TODO: Implement export functionality
            Snackbar.make(binding.root, "Exporting report...", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadDashboardData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate network delay
                kotlinx.coroutines.delay(1000)

                // TODO: Replace with actual data fetching
                withContext(Dispatchers.Main) {
                    updateDashboard()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Failed to load dashboard data")
                }
            }
        }
    }

    private fun updateDashboard() {
        // Update risk level
        updateRiskLevel()
        
        // Update predictions chart
        updatePredictionsChart()
        
        // Update alerts
        updateAlerts()
    }

    private fun updateRiskLevel() {
        // TODO: Implement real risk level calculation
        with(binding) {
            riskLevelIndicator.progress = 65
            tvRiskLevel.text = "Medium Risk Level"
        }
    }

    private fun updatePredictionsChart() {
        // TODO: Implement real prediction data
        setupCharts() // Currently using sample data
    }

    private fun updateAlerts() {
        // TODO: Implement alerts recycler view
    }

    private fun applyFilter(filterType: String) {
        // TODO: Implement filtering logic
        Snackbar.make(binding.root, "Filtering by: $filterType", Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") { loadDashboardData() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
