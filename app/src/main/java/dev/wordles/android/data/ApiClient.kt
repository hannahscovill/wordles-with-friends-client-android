package dev.wordles.android.data

import dev.wordles.android.model.GameState
import dev.wordles.android.model.GuessRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private const val BASE_URL = "https://api.wordles.dev"
private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

class ApiClient(private val sessionId: String) {

    private val client = OkHttpClient()

    fun submitGuess(request: GuessRequest): Result<GameState> = runCatching {
        val body = json.encodeToString(request).toRequestBody(JSON_MEDIA_TYPE)
        val httpRequest = Request.Builder()
            .url("$BASE_URL/guess")
            .addHeader("Cookie", "wordle_session=$sessionId")
            .post(body)
            .build()
        val response = client.newCall(httpRequest).execute()
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        if (!response.isSuccessful) {
            val errorMsg = extractErrorMessage(responseBody) ?: "Request failed (${response.code})"
            throw IOException(errorMsg)
        }
        json.decodeFromString<GameState>(responseBody)
    }

    fun getGameProgress(puzzleDateIsoDay: String): Result<GameState?> = runCatching {
        val httpRequest = Request.Builder()
            .url("$BASE_URL/game/$puzzleDateIsoDay")
            .addHeader("Cookie", "wordle_session=$sessionId")
            .get()
            .build()
        val response = client.newCall(httpRequest).execute()
        if (response.code == 404) return@runCatching null
        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        if (!response.isSuccessful) throw IOException("Request failed (${response.code})")
        json.decodeFromString<GameState>(responseBody)
    }

    private fun extractErrorMessage(body: String): String? = runCatching {
        val obj = json.parseToJsonElement(body)
        obj.toString()
            .substringAfter("\"message\":\"", "")
            .substringBefore("\"")
            .takeIf { it.isNotEmpty() }
    }.getOrNull()
}
