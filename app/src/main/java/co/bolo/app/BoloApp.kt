package co.bolo.app

import android.app.Application
import co.bolo.app.analysis.DictionaryClassifier
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BoloApp : Application() {
    
    @Inject lateinit var dictionaryClassifier: DictionaryClassifier

    override fun onCreate() {
        super.onCreate()
        // Initialize dictionary on startup
        dictionaryClassifier.initialize(this)
    }
}
