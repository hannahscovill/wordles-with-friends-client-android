package dev.wordles.android.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wordles.android.model.GradedLetter
import dev.wordles.android.model.LetterGrade
import dev.wordles.android.ui.theme.WordleGray
import dev.wordles.android.ui.theme.WordleGreen
import dev.wordles.android.ui.theme.WordleKeyUnused
import dev.wordles.android.ui.theme.WordleTileBorder
import dev.wordles.android.ui.theme.WordleTileBorderFilled
import dev.wordles.android.ui.theme.WordleYellow
import kotlin.math.roundToInt

private const val TOTAL_ROWS = 6
private const val WORD_LENGTH = 5

@Composable
fun GameBoard(
    submittedMoves: List<List<GradedLetter>>,
    currentInput: String,
    shakeInput: Boolean,
    modifier: Modifier = Modifier,
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(shakeInput) {
        if (shakeInput) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    10f at 50
                    -10f at 100
                    10f at 150
                    -10f at 200
                    6f at 250
                    -6f at 300
                    0f at 400
                },
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(TOTAL_ROWS) { rowIndex ->
            val isCurrentRow = rowIndex == submittedMoves.size
            val rowOffset = if (isCurrentRow) shakeOffset.value.roundToInt() else 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(rowOffset, 0) },
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(WORD_LENGTH) { colIndex ->
                    val submitted = submittedMoves.getOrNull(rowIndex)
                    val tile: TileState = when {
                        submitted != null -> {
                            val gl = submitted[colIndex]
                            TileState.Graded(gl.letter.uppercase(), gl.grade)
                        }
                        isCurrentRow -> {
                            val ch = currentInput.getOrNull(colIndex)?.uppercaseChar()
                            if (ch != null) TileState.Input(ch.toString()) else TileState.Empty
                        }
                        else -> TileState.Empty
                    }
                    Tile(tile = tile, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

sealed interface TileState {
    data object Empty : TileState
    data class Input(val letter: String) : TileState
    data class Graded(val letter: String, val grade: LetterGrade) : TileState
}

@Composable
private fun Tile(tile: TileState, modifier: Modifier = Modifier) {
    val bgColor: Color
    val borderColor: Color
    val textColor = Color.White
    val letter: String

    when (tile) {
        is TileState.Empty -> {
            bgColor = Color.Transparent
            borderColor = WordleTileBorder
            letter = ""
        }
        is TileState.Input -> {
            bgColor = Color.Transparent
            borderColor = WordleTileBorderFilled
            letter = tile.letter
        }
        is TileState.Graded -> {
            bgColor = when (tile.grade) {
                LetterGrade.CORRECT -> WordleGreen
                LetterGrade.CONTAINED -> WordleYellow
                LetterGrade.WRONG -> WordleGray
            }
            borderColor = Color.Transparent
            letter = tile.letter
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .drawBehind { drawRect(bgColor) }
            .border(2.dp, borderColor)
            .padding(4.dp),
    ) {
        Text(
            text = letter,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
