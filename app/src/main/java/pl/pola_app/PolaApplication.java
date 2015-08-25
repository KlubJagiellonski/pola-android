package pl.pola_app;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import pl.pola_app.BuildConfig;
import timber.log.Timber;

public class PolaApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            //TODO
            Crashlytics.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    Log.e(tag, t.getMessage());
                } else if (priority == Log.WARN) {
                    Log.w(tag, t.getMessage());
                }
            }
        }
    }
}
