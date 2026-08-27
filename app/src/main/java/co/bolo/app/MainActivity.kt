package co.bolo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import co.bolo.app.ui.nav.BoloNavHost
import co.bolo.app.ui.theme.BoloTheme
import co.bolo.app.update.PlayUpdateManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Check for official Google Play Store in-app updates
        PlayUpdateManager.checkForUpdates(this)

        setContent {
            BoloTheme { BoloNavHost() }
        }
    }

    override fun onResume() {
        super.onResume()
        PlayUpdateManager.resumeUpdateIfInProgress(this)
    }
}

