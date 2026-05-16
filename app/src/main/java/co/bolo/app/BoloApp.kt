package co.bolo.app

import android.app.Application
import co.bolo.app.util.EnglishAnalyzer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoloApp : Application() {
    override fun onCreate() {
        super.onCreate()
        EnglishAnalyzer.initialize(this)
    }
}
