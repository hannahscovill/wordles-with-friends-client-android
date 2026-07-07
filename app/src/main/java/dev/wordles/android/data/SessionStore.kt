package dev.wordles.android.data

import android.content.Context
import java.util.UUID

private const val PREFS_NAME = "wordles_prefs"
private const val KEY_SESSION_ID = "session_id"

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val sessionId: String by lazy {
        prefs.getString(KEY_SESSION_ID, null) ?: UUID.randomUUID().toString().also { id ->
            prefs.edit().putString(KEY_SESSION_ID, id).apply()
        }
    }
}
