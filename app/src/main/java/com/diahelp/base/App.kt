package com.diahelp.base

import android.app.Application
import com.diahelp.tools.RealmMigrations
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .schemaVersion(2)
            .migration(RealmMigrations())
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
        // Caching meals data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}