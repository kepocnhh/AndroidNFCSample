package test.android.nfc.module.router

import android.nfc.tech.IsoDep
import android.nfc.tech.TagTechnology
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import test.android.nfc.module.scanner.ScannerScreen
import test.android.nfc.util.toHEX

private object ISOError {
    const val SELECTED_FILE_INVALIDATED   = "6283"
    const val WRONG_LENGTH                = "6700"
    const val FUNCTION_NOT_SUPPORTED      = "6A81"
    const val FILE_NOT_FOUND              = "6A82"
    const val INCORRECT_PARAMETERS_P1_P2  = "6A86"
    const val NORMAL_PROCESSING           = "9000"
}

private fun connect(tt: IsoDep): ByteArray {
    // read record
    val CLA     = 0x00
    val INS     = 0xb2
    // P1 is record number
    // P2 : depend on P1
//    val LC      = ""
//    val data    = ""
    val LE      = 0x00
    // CLA + INS + P1 + P2 + LE
    val data = listOf(
        0x60,
//        0xca,
//        0x00,
//        0x00,
//        0x00,
    ).map { it.toByte() }.toByteArray()
    return tt.use {
        it.connect()
        check(tt.isConnected)
        it.transceive(data)
    }
}

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val ttState = remember { mutableStateOf<TagTechnology?>(null) }
        when (val tt = ttState.value) {
            null -> {
                ScannerScreen(
                    onTagTechnology = {
                        val message = """
                            Tag:
                            ID: ${it.tag.id.toHEX()}
                            Tech list: ${it.tag.techList.toList()}
                        """.trimIndent()
                        println(message)
                        ttState.value = it
                    },
                )
            }
            else -> {
                TODO()
            }
        }
    }
}
