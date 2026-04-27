package com.example.smartcampuscompanion

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class SmartCampusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Subscribe all users to the "announcements" topic automatically on app startup
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOPIC", "Successfully subscribed to announcements topic")
                } else {
                    Log.e("FCM_TOPIC", "Failed to subscribe to announcements topic")
                }
            }
    }
}
