package pl.pola_app.helpers

import android.content.Context
import android.preference.PreferenceManager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import pl.pola_app.TestApplication
import java.lang.Exception

@Config(application = TestApplication::class)
@RunWith(
    RobolectricTestRunner::class
)
class SessionIdTest {
    private val context: Context = RuntimeEnvironment.application
    @Test
    @Throws(Exception::class)
    fun testGetReturnsNotNullWhenCreated() {
        val sessionId = SessionId.create(context).get()
        Assert.assertNotNull(sessionId)
    }

    @Test
    @Throws(Exception::class)
    fun testGetReturnsSameValueWhenCalledTwice() {
        val id1 = SessionId.create(context).get()
        val id2 = SessionId.create(context).get()
        Assert.assertEquals(id1, id2)
    }

    @Test
    @Throws(Exception::class)
    fun testIdIsRefreshedWhenCleared() {
        val firstId = SessionId.create(context).get()
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
        val afterClearId = SessionId.create(context).get()
        Assert.assertNotEquals(firstId, afterClearId)
    }
}