package pl.pola_app.helpers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

/**
 * Creates GUID.
 * It's stored in app's shared preferences so it's recreated when data are cleared.
 */
open class SessionId(context: Context) {
    private val sessionId: String = init(context)
    fun get(): String {
        return sessionId
    }

    companion object {
        private const val PREF_SESSION_GUID = "session_guid"
        private val lock = Any()
        fun create(context: Context): SessionId {
            return SessionId(context)
        }

        private fun getDefaultSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        private fun init(context: Context): String {
            synchronized(lock) {
                val pref =
                    getDefaultSharedPreferences(context)
                var sessionGuid =
                    pref.getString(PREF_SESSION_GUID, null)
                if (sessionGuid == null) {
                    sessionGuid = UUID.randomUUID().toString()
                    val editor = pref.edit()
                    editor.putString(PREF_SESSION_GUID, sessionGuid)
                    editor.apply()
                }
                return sessionGuid
            }
        }
    }

}