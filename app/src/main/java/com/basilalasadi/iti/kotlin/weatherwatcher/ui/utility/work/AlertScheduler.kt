package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class AlertScheduler(context: Context) {
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleAlert(alert: Alert) {
        val alertTime = alert.alertTime.withZoneSameInstant(ZoneId.systemDefault())
        val now = ZonedDateTime.now()
        
        val delay = ChronoUnit.SECONDS.between(now, alertTime).toLong()
        
        val workRequest = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString(AlertWorker.DATA_ALERT, Json.encodeToString(alert))
                    .build()
            )
            .build()
        
        workManager.enqueueUniqueWork(
            (alert.requestCode ?: 0).toString(),
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }
    
    fun rescheduleAlert(alert: Alert) {
        val workRequest = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString(AlertWorker.DATA_ALERT, Json.encodeToString(alert))
                    .build()
            )
            .build()
        
        workManager.enqueueUniqueWork(
            (alert.requestCode ?: 0).toString(),
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }
    
    fun unscheduleAlert(alert: Alert) {
        workManager.cancelAllWorkByTag((alert.requestCode ?: 0).toString())
    }
}

data class AlertData(
    val alert: Alert,
    val cityName: String,
    val alertTime: String,
)
