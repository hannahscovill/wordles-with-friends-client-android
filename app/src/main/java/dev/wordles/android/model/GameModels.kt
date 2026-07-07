package dev.wordles.android.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class LetterGrade {
    @SerialName("correct") CORRECT,
    @SerialName("contained") CONTAINED,
    @SerialName("wrong") WRONG,
}

@Serializable
data class GradedLetter(
    val letter: String,
    val grade: LetterGrade,
)

@Serializable
data class GameState(
    @SerialName("game_id") val gameId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("moves_qty") val movesQty: Int,
    val won: Boolean,
    val moves: List<List<GradedLetter>>,
    val answer: String? = null,
)

@Serializable
data class GuessRequest(
    @SerialName("puzzle_date_iso_day") val puzzleDateIsoDay: String,
    @SerialName("word_guessed") val wordGuessed: String,
)
