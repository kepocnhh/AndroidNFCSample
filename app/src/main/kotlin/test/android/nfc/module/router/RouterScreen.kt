package test.android.nfc.module.router

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import test.android.nfc.MainActivity

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val context = LocalContext.current
        val activity = context as? Activity
        val adapter: NfcAdapter? = remember { mutableStateOf(NfcAdapter.getDefaultAdapter(context)) }.value
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(Unit) {
            lifecycle.currentStateFlow.collect { state ->
                println("state: $state")
            }
        }
        LaunchedEffect(Unit) {
            lifecycle.eventFlow.collect { event ->
                println("event: $event")
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        if (adapter != null && adapter.isEnabled && activity != null) {
                            val intent = Intent(context, activity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
                            val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
//                            val techLists = null
                            val techLists = arrayOf<Array<String>>()
                            adapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
                            println("adapter: $adapter enable foreground dispatch")
                        }
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        if (adapter != null && adapter.isEnabled && activity != null) {
//                            adapter.disableForegroundDispatch(activity)
//                            println("adapter: $adapter disable foreground dispatch")
                        }
                    }
                    else -> {
                        // noop
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            MainActivity.broadcast.collect { broadcast ->
                when (broadcast) {
                    is MainActivity.Broadcast.OnTag -> {
                        println("tag: ${broadcast.tag.techList.toList()}") // todo
                        // todo
                    }
                }
            }
        }
        // todo
    }
}
