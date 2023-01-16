package com.elmacentemobile.view.composable.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elmacentemobile.view.composable.theme.AppBlueColorLight

@Composable
fun Dot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = color)
    )
}

@Preview(widthDp = 360, showBackground = true)
@Composable
fun PreviewDot() {

    Dot(
        color = AppBlueColorLight,
        modifier = Modifier
            .padding(all = 32.dp)
            .requiredSize(32.dp)
    )

}