package test.android.nfc.util

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build

internal object NfcAdapterUtil {
    private const val TAG = "[NFCUtil]"

    fun stop(adapter: NfcAdapter, activity: Activity) {
        check(adapter.isEnabled)
        adapter.disableForegroundDispatch(activity)
        println("$TAG: adapter: $this disable foreground dispatch")
    }

    fun dispatch(adapter: NfcAdapter, activity: Activity) {
        check(adapter.isEnabled)
        val intent = Intent(activity, activity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        val techLists = arrayOf<Array<String>>()
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
        println("$TAG: adapter: $this enable foreground dispatch")
    }

    fun getTag(intent: Intent): Tag? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
    }
}
