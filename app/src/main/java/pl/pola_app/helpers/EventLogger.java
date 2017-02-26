package pl.pola_app.helpers;

import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.SearchEvent;

import pl.pola_app.BuildConfig;

public class EventLogger {

    public void logSearch(String result, String deviceId) {
        if (!BuildConfig.USE_CRASHLYTICS) {
            return;
        }

        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(result)
                .putCustomAttribute("DeviceId", deviceId)
        );
    }

    public void logCustom(String eventName, Pair<String, String> attribute) {
        if (!BuildConfig.USE_CRASHLYTICS) {
            return;
        }

        Answers.getInstance().logCustom(new CustomEvent(eventName)
                .putCustomAttribute(attribute.first, attribute.second));
    }

    public void logContentView(String contentName, String contentType, String contentId, String code, String deviceId) {
        if (!BuildConfig.USE_CRASHLYTICS) {
            return;
        }

        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(contentName) //As it might be null
                    .putContentType(contentType)
                    .putContentId(contentId)
                    .putCustomAttribute("Code", code)
                    .putCustomAttribute("DeviceId", deviceId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logException(Throwable throwable) {
        if (!BuildConfig.USE_CRASHLYTICS) {
            return;
        }

        try {
            Crashlytics.logException(throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
