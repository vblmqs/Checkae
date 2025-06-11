package com.example.taskapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskapp.ui.TaskApp
import androidx.compose.runtime.collectAsState
import com.example.taskapp.ui.theme.TaskAppTheme
import com.example.taskapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeViewModel = ThemeViewModel(application)

        setContent {
            val isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value

            TaskAppTheme(darkTheme = isDarkTheme) {
                TaskApp(themeViewModel)
            }
        }
    }
}