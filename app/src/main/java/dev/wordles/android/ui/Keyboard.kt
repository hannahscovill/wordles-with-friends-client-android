package dev.wordles.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wordles.android.model.LetterGrade
import dev.wordles.android.ui.theme.WordleGray
import dev.wordles.android.ui.theme.WordleGreen
import dev.wordles.android.ui.theme.WordleKeyUnused
import dev.wordles.android.ui.theme.WordleYellow

private val ROWS = listOf(
    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L"),
    listOf("⌫", "Z", "X", "C", "V", "B", "N", "M", "↵"),
)

@Composable
fun Keyboard(
    keyStates: Map<String, LetterGrade>,
    onKeyPress: (String) -> Unit,
    onEnter: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (row in ROWS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, androidx.compose.ui.Alignment.CenterHorizontally),
            ) {
                for (key in row) {
                    val grade = keyStates[key]
                    val bgColor = when (grade) {
                        LetterGrade.CORRECT -> WordleGreen
                        LetterGrade.CONTAINED -> WordleYellow
                        LetterGrade.WRONG -> WordleGray
                        null -> WordleKeyUnused
                    }
                    val isWide = key == "⌫" || key == "↵"
                    Button(
                        onClick = {
                            when (key) {
                                "⌫" -> onBackspace()
                                "↵" -> onEnter()
                                else -> onKeyPress(key)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .then(
                                if (isWide) Modifier.widthIn(min = 48.dp)
                                else Modifier.widthIn(min = 36.dp)
                            )
                            .weight(if (isWide) 1.5f else 1f),
                    ) {
                        Text(
                            text = key,
                            color = Color.White,
                            fontSize = if (isWide) 14.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
