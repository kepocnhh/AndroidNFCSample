package test.android.nfc.module.tag

import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import android.nfc.tech.TagTechnology
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import test.android.nfc.App
import test.android.nfc.util.compose.BackHandler
import test.android.nfc.util.compose.Button
import test.android.nfc.util.compose.Text
import test.android.nfc.util.showToast
import test.android.nfc.util.toHEX

private object ISOError {
    const val SELECTED_FILE_INVALIDATED   = "6283"
    const val WRONG_LENGTH                = "6700"
    const val FUNCTION_NOT_SUPPORTED      = "6A81"
    const val FILE_NOT_FOUND              = "6A82"
    const val INCORRECT_PARAMETERS_P1_P2  = "6A86"
    const val NORMAL_PROCESSING           = "9000"
}

private fun transceive(tt: IsoDep, data: ByteArray): Result<ByteArray> {
    return runCatching {
        tt.use {
            if (!tt.isConnected) it.connect()
            println("transceive: ${data.toHEX()}")
            it.transceive(data)
        }
    }
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
internal fun TagScreen(
    tt: IsoDep,
    onForget: () -> Unit,
) {
    BackHandler(block = onForget)
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
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
                Text(
                    text = "ID: " + tt.tag.id.toHEX(),
                )
                Text(
                    text = "Tech list:" + tt.tag.techList.toList(),
                )
                Button(
                    text = "command",
                    onClick = {
                        val data = listOf(0xa8, 0x00, 0x90, 0x00, 0x2d, 0xcf, 0x46, 0x29, 0x04, 0xb4, 0x78, 0xd8, 0x68, 0xa7, 0xff, 0x3f, 0x2b, 0xf1, 0xfc)
                            .map { it.toByte() }
                            .toByteArray()
                        transceive(tt = tt, data = data)
                            .fold(
                                onSuccess = { bytes ->
                                    println("Transceive success: ${bytes.toHEX()}")
                                },
                                onFailure = { error ->
                                    println("Transceive error: $error")
                                    error.printStackTrace()
                                    if (error is TagLostException) {
                                        println("tag lost: ${error.message}")
                                    }
                                    context.showToast("Transceive error: $error")
                                },
                            )
                    },
                )
                Button(
                    text = "forget",
                    onClick = onForget,
                )
            }
        }
    }
}
