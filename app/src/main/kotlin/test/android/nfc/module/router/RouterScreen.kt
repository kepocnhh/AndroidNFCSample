package test.android.nfc.module.router

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import test.android.nfc.App
import test.android.nfc.MainActivity

private fun NfcAdapter.stop(activity: Activity) {
    if (!isEnabled) return
    disableForegroundDispatch(activity)
    println("adapter: $this disable foreground dispatch")
}

private fun NfcAdapter.dispatch(activity: Activity) {
    if (!isEnabled) return
    val intent = Intent(activity, activity::class.java)
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
    val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
    val techLists = arrayOf<Array<String>>()
    enableForegroundDispatch(activity, pendingIntent, filters, techLists)
    println("adapter: $this enable foreground dispatch")
}

private object RouterScreen {
    sealed interface State {
        data object Stopped : State
        data object NoTag : State
        class OnTag(val tt: IsoDep) : State
    }
}

@Composable
private fun Button(text: String, onClick: () -> Unit) {
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .wrapContentSize(),
        text = text,
    )
}

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val context = LocalContext.current
        val activity = context as Activity // todo
        val adapter: NfcAdapter? = remember { mutableStateOf(NfcAdapter.getDefaultAdapter(context)) }.value
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(Unit) {
            lifecycle.currentStateFlow.collect { state ->
                println("state: $state")
            }
        }
        val state = remember { mutableStateOf<RouterScreen.State>(RouterScreen.State.Stopped) }
        LaunchedEffect(Unit) {
            lifecycle.eventFlow.collect { event ->
                println("event: $event")
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        state.value = RouterScreen.State.Stopped
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
                        val tt: IsoDep? = IsoDep.get(broadcast.tag)
                        if (tt == null) {
                            println("Wrong tag!")
                        } else {
                            if (adapter == null) TODO()
                            adapter.stop(activity)
                            state.value = RouterScreen.State.OnTag(tt = tt)
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
                when (val type = state.value) {
                    RouterScreen.State.Stopped -> {
                        Button(
                            text = "dispatch",
                            onClick = {
                                if (adapter == null) {
                                    println("No NFC adapter!")
                                } else {
                                    adapter.dispatch(activity)
                                    state.value = RouterScreen.State.NoTag
                                }
                            },
                        )
                    }
                    RouterScreen.State.NoTag -> {
                        Button(
                            text = "stop",
                            onClick = {
                                if (adapter == null) TODO()
                                adapter.stop(activity)
                                state.value = RouterScreen.State.Stopped
                            },
                        )
                    }
                    is RouterScreen.State.OnTag -> {
                        BasicText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            text = "Tech list: ${type.tt.tag.techList.toList()}",
                        )
                        // todo connect
                        Button(
                            text = "forget",
                            onClick = {
                                state.value = RouterScreen.State.Stopped
                            },
                        )
                    }
                }
            }
        }
    }
}
