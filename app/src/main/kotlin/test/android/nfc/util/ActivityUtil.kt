package test.android.nfc.util

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow

internal object ActivityUtil {
    sealed interface Broadcast {
        class OnNewIntent(val activity: Activity, val intent: Intent) : Broadcast
    }

    val broadcast = MutableSharedFlow<Broadcast>()
}
