package com.example.smartcampuscompanion

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.smartcampuscompanion.ui.navigation.AppNavigation
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(sessionManager.isDarkMode()) }
            
            SmartCampusCompanionTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { enabled ->
                        sessionManager.setDarkMode(enabled)
                        isDarkMode = enabled
                    }
                )
            }
        }
    }
}
