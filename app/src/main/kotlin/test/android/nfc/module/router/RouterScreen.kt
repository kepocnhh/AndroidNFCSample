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
import test.android.nfc.module.tag.TagScreen
import test.android.nfc.util.toHEX

@Composable
internal fun RouterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val ttState = remember { mutableStateOf<IsoDep?>(null) }
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
                TagScreen(
                    tt = tt,
                    onForget = {
                      ttState.value = null
                    },
                )
            }
        }
    }
}
