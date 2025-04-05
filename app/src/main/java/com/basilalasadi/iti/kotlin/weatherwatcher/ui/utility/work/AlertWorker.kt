package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.Alert
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.repository.CityRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.local.CityLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.CityRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.source.remote.api.CityApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.database.AppDatabase
import com.basilalasadi.iti.kotlin.weatherwatcher.data.settings.SettingsRepository
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.repository.WeatherRepositoryImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.local.WeatherLocalDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.WeatherRemoteDataSourceImpl
import com.basilalasadi.iti.kotlin.weatherwatcher.data.weather.source.remote.api.WeatherApiService
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.core.MainActivity
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.currentweather.viewmodel.CurrentWeatherViewModelMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class AlertWorker(
    context: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    
    private val soundUri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${applicationContext.packageName}/${R.raw.alert}".toUri()
    val notificationManager = NotificationManagerCompat.from(applicationContext)
    
    @OptIn(FlowPreview::class)
    override suspend fun doWork(): Result {
        try {
            val alert = getAlert()
            
            val database = AppDatabase.getInstance(applicationContext)
            val weatherDao = database.getWeatherDao()
            val cityDao = database.getCityDao()
            val alertDao = database.getAlertDao()
            
            val weatherApiService = WeatherApiService.create()
            val cityApiService = CityApiService.create()
            
            val weatherRepository = WeatherRepositoryImpl(
                localDataSource = WeatherLocalDataSourceImpl(weatherDao),
                remoteDataSource = WeatherRemoteDataSourceImpl(weatherApiService)
            )
            val cityRepository = CityRepositoryImpl(
                localDataSource = CityLocalDataSourceImpl(cityDao, alertDao),
                remoteDataSource = CityRemoteDataSourceImpl(cityApiService),
            )
            val settingsRepository = SettingsRepository(
                applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), 0)
            )
            
            val weather = weatherRepository.getLatestWeatherFlow(alert.city)
                .timeout(60.0.seconds)
                .catch { emit(null) }
                .first()
            
            if (weather != null) {
                val isNight = weather.dateTime.hour !in 6..19
                
                val mappedWeather = CurrentWeatherViewModelMapper(alert.city, settingsRepository.get()).map(weather, emptyList())
                
                with(weather.value) {
                    val conditionTitle = applicationContext.getString(condition.title)
                    val conditionDescription = applicationContext.getString(condition.description)
                    val conditionIcon = if (isNight) condition.nightIcon else condition.icon
                    
                    val temperature = "%.0f %s".format(
                        mappedWeather.weatherDisplay?.currentTemperature ?: 0.0,
                        mappedWeather.weatherDisplay?.temperatureUnit?.let { applicationContext.getString(it) } ?: "",
                    )
                    val cityName = mappedWeather.weatherDisplay?.cityName ?: ""
                    
                    val title = applicationContext.getString(R.string.template_notification_title)
                        .format(conditionTitle, temperature, cityName)
                    
                    val content = applicationContext.getString(R.string.template_notification_content).format(conditionDescription)
                    
                    cityRepository.removeAlert(alert)
                    
                    createNotificationChannel()
                    val notificationId = sendNotification(alert, conditionIcon, title, content)
                    
                    if (notificationId == -1) {
                        return Result.failure()
                    }
                    
                    return Result.success()
                }
            } else {
                return Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "doWork: ", ex)
            return Result.failure()
        }
    }
    
    private fun getAlert(): Alert {
        val json = workerParams.inputData.getString(DATA_ALERT)
        
        if (json != null) {
            return Json.decodeFromString<Alert>(json)
        } else {
            throw IllegalArgumentException("alert must be set in inputData.")
        }
    }
    
    private fun createNotificationChannel() {
        val name = "Alerts"
        val descriptionText = "Alerts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setSound(
                soundUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun sendNotification(
        alert: Alert,
        icon: Int,
        title: String,
        content: String,
    ): Int {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_IMMUTABLE,
                )
            )
            .addAction(
                R.drawable.baseline_done_24,
                applicationContext.getString(R.string.done),
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(applicationContext, AlertActionReceiver::class.java).apply {
                        action = AlertActionReceiver.ACTION_DONE
                        putExtra(AlertActionReceiver.EXTRA_REQUEST_CODE, alert.requestCode ?: 0)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.baseline_snooze_24,
                applicationContext.getString(R.string.snooze),
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(applicationContext, AlertActionReceiver::class.java).apply {
                        action = AlertActionReceiver.ACTION_SNOOZE
                        putExtra(AlertActionReceiver.EXTRA_REQUEST_CODE, alert.requestCode ?: 0)
                        putExtra(AlertActionReceiver.EXTRA_ALERT, Json.encodeToString(alert))
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
        
        
        if (applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return -1
        }
        
        val notificationId = alert.requestCode ?: 0
        notificationManager.notify(notificationId, notification)
        
        return notificationId
    }
    
    private suspend fun playSoundUntilDismiss(notificationId: Int) {
        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alert)
        mediaPlayer.isLooping = true
        
        delay(mediaPlayer.duration.milliseconds)
        mediaPlayer.start()
        
        val startTime = System.nanoTime().nanoseconds
        
        while (
            notificationManager.activeNotifications.any { it.id == notificationId } &&
            (System.nanoTime().nanoseconds - startTime) < 3.0.minutes
        ) {
            delay(1000)
        }
        
        mediaPlayer.stop()
    }
    
    companion object {
        private const val TAG = "AlertWorker"
        
        const val DATA_ALERT = "alert"
        
        private const val CHANNEL_ID = "alerts"
    }
}