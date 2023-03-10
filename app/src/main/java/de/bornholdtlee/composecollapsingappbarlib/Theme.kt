package de.bornholdtlee.composecollapsingappbarlib

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColors(
    primary = Color(0xff00de71),
    primaryVariant = Color(0xff94ffbb)
)

@Composable
fun ComposeCollapsingAppBarLibTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        content = content
    )
}
