package pl.pola_app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pl.pola_app.helpers.Utils;
import pl.pola_app.internal.di.PolaComponent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class PolaApplication extends MultiDexApplication {

    private PolaComponent component;
    public static Retrofit retrofit;

    @Override public void onCreate() {
        super.onCreate();

        component = PolaComponent.Initializer.init(this);
        if(BuildConfig.USE_FIREBASE) {
            FirebaseAnalytics.getInstance(this);
        }
        ButterKnife.setDebug(BuildConfig.DEBUG);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Utils.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Utils.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(this.getResources().getString(R.string.pola_api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
    }

    public static PolaComponent component(Context context) {
        return ((PolaApplication) context.getApplicationContext()).component;
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NotNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if(BuildConfig.USE_FIREBASE) {
                FirebaseCrashlytics.getInstance().log("E/" + tag + ":" + message);
                if(t != null) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                }
            }

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
