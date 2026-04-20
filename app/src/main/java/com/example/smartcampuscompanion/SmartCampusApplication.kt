package com.example.smartcampuscompanion

import android.app.Application
import com.google.firebase.FirebaseApp

class SmartCampusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
