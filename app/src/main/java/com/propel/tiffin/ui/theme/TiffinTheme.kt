
package com.propel.tiffin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TiffinColors = lightColorScheme(
    primary = Color(0xFF245C45),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCECE0),
    onPrimaryContainer = Color(0xFF173D2D),
    secondary = Color(0xFFB87935),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFAF8F3),
    onBackground = Color(0xFF202820),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF202820),
    surfaceVariant = Color(0xFFF0EEE7),
    onSurfaceVariant = Color(0xFF656A62),
    outline = Color(0xFFDCDDD5),
    error = Color(0xFFBA3434)
)

@Composable
fun TiffinTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TiffinColors,
        content = content
    )
}
