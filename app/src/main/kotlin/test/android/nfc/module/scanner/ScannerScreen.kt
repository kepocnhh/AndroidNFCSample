package test.android.nfc.module.scanner

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.nfc.tech.TagTechnology
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import test.android.nfc.App
import test.android.nfc.util.ActivityUtil
import test.android.nfc.util.NfcAdapterUtil
import test.android.nfc.util.compose.Button

private fun tryFindTag(intent: Intent, onTagTechnology: (TagTechnology) -> Unit) {
    if (intent.action != NfcAdapter.ACTION_TAG_DISCOVERED) {
        println("No action.")
        return
    }
    val tag = NfcAdapterUtil.getTag(intent)
    if (tag == null) {
        println("No tag.")
        return
    }
    val tt = IsoDep.get(tag)
    if (tt == null) {
        println("Wrong tag!")
        return
    }
    onTagTechnology(tt)
}
@Composable
internal fun ScannerScreen(
    onTagTechnology: (TagTechnology) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val context = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        val activity = context as Activity // todo
        val adapter: NfcAdapter? = remember { mutableStateOf(NfcAdapter.getDefaultAdapter(context)) }.value
        val isStartedState = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            lifecycle.eventFlow.collect { event ->
                println("event: $event") // todo
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        isStartedState.value = false
                    }
                    else -> {
                        // noop
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            ActivityUtil.broadcast.collect { broadcast ->
                when (broadcast) {
                    is ActivityUtil.Broadcast.OnNewIntent -> {
                        if (activity == broadcast.activity) {
                            tryFindTag(broadcast.intent) { tt ->
                                if (adapter == null) TODO()
                                NfcAdapterUtil.stop(adapter, activity)
                                isStartedState.value = false
                                onTagTechnology(tt)
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(App.Theme.insets),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                if (isStartedState.value) {
                    Button(
                        text = "stop",
                        onClick = {
                            if (adapter == null) TODO()
                            NfcAdapterUtil.stop(adapter, activity)
                            isStartedState.value = false
                        },
                    )
                } else {
                    Button(
                        text = "dispatch",
                        onClick = {
                            if (adapter == null) {
                                println("No NFC adapter!")
                            } else {
                                NfcAdapterUtil.dispatch(adapter, activity)
                                isStartedState.value = true
                            }
                        },
                    )
                }
            }
        }
    }
}
