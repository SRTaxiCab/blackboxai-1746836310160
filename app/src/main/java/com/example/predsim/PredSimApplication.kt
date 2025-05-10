package com.example.predsim

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.predsim.data.repository.PredictiveRepository
import com.example.predsim.utils.SecurityManager

class PredSimApplication : Application() {

    lateinit var securityManager: SecurityManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize SecurityManager
        securityManager = SecurityManager.getInstance(this)
        
        // Initialize Repository
        PredictiveRepository.getInstance()
        
        // Create notification channels
        createNotificationChannels()
        
        // Initialize crash reporting (TODO: Implement proper crash reporting)
        setupCrashReporting()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // High priority channel for critical alerts
            val criticalChannel = NotificationChannel(
                CHANNEL_ID_CRITICAL,
                "Critical Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical security and prediction alerts"
                enableVibration(true)
                enableLights(true)
            }

            // Normal priority channel for regular updates
            val updatesChannel = NotificationChannel(
                CHANNEL_ID_UPDATES,
                "Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Regular prediction updates and notifications"
            }

            // Get the NotificationManager
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Create the channels
            notificationManager.createNotificationChannels(listOf(criticalChannel, updatesChannel))
        }
    }

    private fun setupCrashReporting() {
        // TODO: Implement crash reporting service
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Log the crash
            android.util.Log.e(TAG, "Uncaught exception in thread $thread", throwable)
            
            // TODO: Send crash report to analytics service
        }
    }

    companion object {
        private const val TAG = "PredSimApplication"
        const val CHANNEL_ID_CRITICAL = "critical_alerts"
        const val CHANNEL_ID_UPDATES = "updates"

        @Volatile
        private var instance: PredSimApplication? = null

        fun getInstance(): PredSimApplication {
            return instance ?: synchronized(this) {
                instance ?: throw IllegalStateException("Application not initialized")
            }
        }
    }

    // App-wide configuration
    object Config {
        const val API_BASE_URL = "https://api.predsim.com/" // TODO: Replace with actual API URL
        const val DATABASE_NAME = "predsim_db"
        const val DATABASE_VERSION = 1
        
        // Feature flags
        var ENABLE_OFFLINE_MODE = false
        var ENABLE_DEBUG_LOGGING = BuildConfig.DEBUG
        var ENABLE_ANALYTICS = !BuildConfig.DEBUG
        
        // Security settings
        const val TOKEN_EXPIRY_HOURS = 24
        const val MAX_LOGIN_ATTEMPTS = 3
        const val PASSWORD_MIN_LENGTH = 8
        
        // Data refresh intervals (in milliseconds)
        const val PREDICTION_REFRESH_INTERVAL = 5 * 60 * 1000L  // 5 minutes
        const val ALERT_REFRESH_INTERVAL = 2 * 60 * 1000L      // 2 minutes
        
        // Chart configuration
        const val MAX_CHART_DATA_POINTS = 100
        const val CHART_ANIMATION_DURATION = 1000
    }

    // App-wide preferences
    object Preferences {
        private const val PREFS_NAME = "predsim_prefs"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_USER_REGION = "user_region"
        
        fun getLastSyncTime(context: Context): Long {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_SYNC, 0)
        }
        
        fun setLastSyncTime(context: Context, timestamp: Long) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_SYNC, timestamp)
                .apply()
        }
        
        fun getUserRegion(context: Context): String? {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_REGION, null)
        }
        
        fun setUserRegion(context: Context, region: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_USER_REGION, region)
                .apply()
        }
    }
}
