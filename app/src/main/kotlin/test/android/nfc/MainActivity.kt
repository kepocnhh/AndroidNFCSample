package test.android.nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import test.android.nfc.module.router.RouterScreen
import test.android.nfc.util.compose.BackHandler

internal class MainActivity : AppCompatActivity() {
    sealed interface Broadcast {
        class OnTag(val tag: Tag) : Broadcast
    }

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
        println("$TAG: ${intent.action}")
        when (intent.action) {
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
                } else {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                }
                if (tag == null) return
//                MifareUltralight.get(tag) // todo
                lifecycleScope.launch {
                    _broadcast.emit(Broadcast.OnTag(tag = tag))
                }
            }
        }
    }

    companion object {
        private const val TAG = "[Main]"
        private val _broadcast = MutableSharedFlow<Broadcast>()
        val broadcast = _broadcast.asSharedFlow()
    }
}
