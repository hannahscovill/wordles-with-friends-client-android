package dev.wordles.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.wordles.android.ui.theme.WordleGreen
import dev.wordles.android.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Header
        Text(
            text = "Wordles",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
        )

        // Board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            GameBoard(
                submittedMoves = state.submittedMoves,
                currentInput = state.currentInput,
                shakeInput = state.shakeInput,
            )
        }

        // Error snackbar
        if (state.errorMessage != null) {
            Snackbar(
                action = {
                    TextButton(onClick = viewModel::dismissError) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(8.dp),
            ) {
                Text(state.errorMessage ?: "")
            }
        }

        // Win / loss banner
        if (state.won || state.lost) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Text(
                    text = if (state.won) "You got it!" else "Better luck next time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.won) WordleGreen else MaterialTheme.colorScheme.onSurface,
                )
                if (state.answer != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "The word was: ${state.answer!!.uppercase()}",
                        fontSize = 16.sp,
                    )
                }
            }
        }

        // Loading indicator
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 8.dp),
            )
        }

        // Keyboard
        Keyboard(
            keyStates = state.keyStates,
            onKeyPress = viewModel::onKeyPress,
            onEnter = viewModel::onEnter,
            onBackspace = viewModel::onBackspace,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}
