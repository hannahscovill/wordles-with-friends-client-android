package dev.wordles.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wordles.android.data.ApiClient
import dev.wordles.android.model.GameState
import dev.wordles.android.model.GradedLetter
import dev.wordles.android.model.GuessRequest
import dev.wordles.android.model.LetterGrade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class UiState(
    val currentInput: String = "",
    val submittedMoves: List<List<GradedLetter>> = emptyList(),
    val keyStates: Map<String, LetterGrade> = emptyMap(),
    val isLoading: Boolean = false,
    val won: Boolean = false,
    val lost: Boolean = false,
    val answer: String? = null,
    val errorMessage: String? = null,
    val shakeInput: Boolean = false,
    val puzzleDate: String = LocalDate.now().toString(),
)

class GameViewModel(private val apiClient: ApiClient) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadGameProgress()
    }

    private fun loadGameProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            val date = _uiState.value.puzzleDate
            apiClient.getGameProgress(date).onSuccess { state ->
                if (state != null) applyServerState(state)
            }
        }
    }

    fun onKeyPress(letter: String) {
        _uiState.update { state ->
            if (state.currentInput.length < 5 && !state.won && !state.lost) {
                state.copy(currentInput = state.currentInput + letter, errorMessage = null)
            } else state
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            if (state.currentInput.isNotEmpty()) {
                state.copy(currentInput = state.currentInput.dropLast(1), errorMessage = null)
            } else state
        }
    }

    fun onEnter() {
        val state = _uiState.value
        if (state.currentInput.length != 5 || state.isLoading || state.won || state.lost) {
            if (state.currentInput.length != 5) triggerShake()
            return
        }
        submitGuess(state.currentInput, state.puzzleDate)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun submitGuess(word: String, date: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            val result = apiClient.submitGuess(GuessRequest(date, word.lowercase()))
            result.onSuccess { serverState ->
                applyServerState(serverState)
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                triggerShake()
            }
        }
    }

    private fun applyServerState(serverState: GameState) {
        val newKeyStates = buildKeyStates(serverState.moves)
        val lost = !serverState.won && serverState.movesQty >= 6
        _uiState.update { state ->
            state.copy(
                currentInput = "",
                submittedMoves = serverState.moves,
                keyStates = newKeyStates,
                isLoading = false,
                won = serverState.won,
                lost = lost,
                answer = serverState.answer,
            )
        }
    }

    private fun triggerShake() {
        _uiState.update { it.copy(shakeInput = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            _uiState.update { it.copy(shakeInput = false) }
        }
    }

    private fun buildKeyStates(moves: List<List<GradedLetter>>): Map<String, LetterGrade> {
        val result = mutableMapOf<String, LetterGrade>()
        for (move in moves) {
            for (gradedLetter in move) {
                val key = gradedLetter.letter.uppercase()
                val existing = result[key]
                val grade = gradedLetter.grade
                // Priority: correct > contained > wrong
                if (existing == null ||
                    (existing != LetterGrade.CORRECT && grade == LetterGrade.CORRECT) ||
                    (existing == LetterGrade.WRONG && grade == LetterGrade.CONTAINED)
                ) {
                    result[key] = grade
                }
            }
        }
        return result
    }
}
