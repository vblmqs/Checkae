package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskapp.ui.TaskApp
import androidx.compose.runtime.collectAsState
import androidx.work.*
import java.util.concurrent.TimeUnit
import com.example.taskapp.notifications.DeadlineNotificationWorker
import com.example.taskapp.ui.theme.TaskAppTheme
import com.example.taskapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val workRequest = PeriodicWorkRequestBuilder<DeadlineNotificationWorker>(
            6, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "deadline_checker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val themeViewModel = ThemeViewModel(application)

        setContent {
            val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

            TaskAppTheme(darkTheme = isDarkTheme) {
                TaskApp(themeViewModel)
            }
        }
    }
}