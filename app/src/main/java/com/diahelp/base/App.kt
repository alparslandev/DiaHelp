package com.diahelp.base

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Caching data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}