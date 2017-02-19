package pl.pola_app.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import pl.pola_app.TestApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@Config(application = TestApplication.class)
@RunWith(RobolectricGradleTestRunner.class)
public class SessionIdTest {
    private final Context context = RuntimeEnvironment.application;

    @Test
    public void testGetReturnsNotNullWhenCreated() throws Exception {
        final String sessionId = SessionId.create(context).get();

        assertNotNull(sessionId);
    }

    @Test
    public void testGetReturnsSameValueWhenCalledTwice() throws Exception {
        final String id1 = SessionId.create(context).get();
        final String id2 = SessionId.create(context).get();

        assertEquals(id1, id2);
    }

    @Test
    public void testIdIsRefreshedWhenCleared() throws Exception {
        final String firstId = SessionId.create(context).get();
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        final String afterClearId = SessionId.create(context).get();

        assertNotEquals(firstId, afterClearId);
    }
}