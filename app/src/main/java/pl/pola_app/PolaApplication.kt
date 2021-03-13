package pl.pola_app

import android.app.Application
import android.content.Context
import android.util.Log
import butterknife.ButterKnife
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.pola_app.helpers.TIMEOUT_SECONDS
import pl.pola_app.internal.di.PolaComponent
import pl.pola_app.internal.di.PolaComponent.Initializer.init
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.TimeUnit

class PolaApplication : Application() {
    private lateinit var component: PolaComponent
    override fun onCreate() {
        super.onCreate()
        component = init(this)
        if (BuildConfig.USE_FIREBASE) {
            FirebaseAnalytics.getInstance(this)
        }
        ButterKnife.setDebug(BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(this.resources.getString(R.string.pola_api_url))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(client)
            .build()
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (BuildConfig.USE_FIREBASE) {
                FirebaseCrashlytics.getInstance().log("E/$tag:$message")
                if (t != null) {
                    FirebaseCrashlytics.getInstance().recordException(t)
                }
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    Log.e(tag, t.message)
                } else if (priority == Log.WARN) {
                    Log.w(tag, t.message)
                }
            }
        }
    }

    companion object {
        lateinit var retrofit: Retrofit
        fun component(context: Context): PolaComponent {
            return (context.applicationContext as PolaApplication).component
        }
    }
}