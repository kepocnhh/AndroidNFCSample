package test.android.nfc.module.tag

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
import test.android.nfc.App
import test.android.nfc.util.compose.BackHandler
import test.android.nfc.util.compose.Button
import test.android.nfc.util.compose.Text
import test.android.nfc.util.toHEX

@Composable
internal fun TagScreen(
    tt: TagTechnology,
    onForget: () -> Unit,
) {
    BackHandler(block = onForget)
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
                    text = "forget",
                    onClick = onForget,
                )
            }
        }
    }
}
