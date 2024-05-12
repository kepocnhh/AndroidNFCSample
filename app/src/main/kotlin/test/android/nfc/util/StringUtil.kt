package test.android.nfc.util

import kotlin.experimental.and

internal fun ByteArray.toHEX(): String {
    return joinToString(separator = "-") { String.format("%02x", it and 0xff.toByte()) }
}