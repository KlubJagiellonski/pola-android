package pl.pola_app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import pl.pola_app.internal.di.PolaComponent;
import timber.log.Timber;

public class PolaApplication extends Application {

    private PolaComponent component;

    @Override public void onCreate() {
        super.onCreate();

        component = PolaComponent.Initializer.init(this);

        Fabric.with(this, new Crashlytics());
        ButterKnife.setDebug(BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    public static PolaComponent component(Context context) {
        return ((PolaApplication) context.getApplicationContext()).component;
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            
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
