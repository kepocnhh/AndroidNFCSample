package test.android.nfc.util

internal fun ByteArray.toHEX(): String {
    return joinToString(separator = "-") { String.format("%02x", it.toInt() and 0xff) }
}
