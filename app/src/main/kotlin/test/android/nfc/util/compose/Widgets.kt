package test.android.nfc.util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun Button(text: String, onClick: () -> Unit) {
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
internal fun Text(
    text: String,
    paddings: PaddingValues = PaddingValues(all = 8.dp),
) {
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddings),
        text = text,
    )
}
