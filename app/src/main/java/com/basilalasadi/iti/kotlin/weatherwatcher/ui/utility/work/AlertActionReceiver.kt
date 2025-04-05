package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import kotlinx.serialization.json.Json

class AlertActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = NotificationManagerCompat.from(context)
        
        notificationManager.cancelAll()
        
        when (intent.action) {
            ACTION_SNOOZE -> {
                val json = intent.getStringExtra(EXTRA_ALERT)
                
                if (json != null) {
                    val alert = Json.decodeFromString<Alert>(json)
                    AlertScheduler(context).rescheduleAlert(alert)
                }
            }
        }
    }
    
    companion object {
        const val ACTION_DONE = "done"
        const val ACTION_SNOOZE = "snooze"
        
        const val EXTRA_REQUEST_CODE = "requestCode"
        const val EXTRA_ALERT = "alert"
    }
}