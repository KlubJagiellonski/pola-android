package pl.pola_app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.UUID;

/**
 * Creates GUID.
 * It's stored in app's shared preferences so it's recreated when data are cleared.
 */
public class SessionId {
    private static final String PREF_SESSION_GUID = "session_guid";
    private static final Object lock = new Object();
    private final String sessionId;

    public static SessionId create(Context context) {
        return new SessionId(context);
    }

    private SessionId(Context context) {
        sessionId = init(context);
    }

    public String get() {
        return sessionId;
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static String init(Context context) {
        synchronized (lock) {
            SharedPreferences pref = getDefaultSharedPreferences(context);
            String sessionGuid = pref.getString(PREF_SESSION_GUID, null);

            if (sessionGuid == null) {
                sessionGuid = UUID.randomUUID().toString();
                final SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_SESSION_GUID, sessionGuid);
                editor.apply();
            }
            return sessionGuid;
        }
    }
}
