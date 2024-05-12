package test.android.nfc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import test.android.nfc.module.router.RouterScreen
import test.android.nfc.util.ActivityUtil
import test.android.nfc.util.compose.BackHandler

internal class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ComposeView(this)
        setContentView(view)
        view.setContent {
            App.Theme.Composition(
                onBackPressedDispatcher = onBackPressedDispatcher,
            ) {
                BackHandler {
                    finish()
                }
                RouterScreen()
            }
        }
    }

    override fun onPause() {
        println("$TAG: on pre pause...")
        super.onPause()
        println("$TAG: on pause...")
    }

    override fun onResume() {
        println("$TAG: on pre resume...")
        super.onResume()
        println("$TAG: on resume...")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        val activity: Activity = this
        lifecycleScope.launch {
            ActivityUtil.broadcast.emit(ActivityUtil.Broadcast.OnNewIntent(activity = activity, intent = intent))
        }
    }

    companion object {
        private const val TAG = "[Main]"
    }
}
