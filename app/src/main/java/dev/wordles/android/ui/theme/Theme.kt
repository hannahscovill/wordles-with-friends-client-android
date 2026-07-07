package dev.wordles.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = WordleGreen,
    background = WordleBackground,
    surface = WordleSurface,
    onSurface = WordleOnSurface,
    onBackground = WordleOnSurface,
)

@Composable
fun WordlesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
