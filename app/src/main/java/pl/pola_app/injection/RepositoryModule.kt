package pl.pola_app.injection

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.pola_app.BuildConfig
import pl.pola_app.repository.PermissionHandler
import pl.pola_app.repository.PermissionHandlerImpl
import pl.pola_app.repository.PolaApi
import pl.pola_app.repository.SessionId
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Discoverable // makes this module be automatically included in the BYOD apk build
@Module(
    includes = [
        ActivityBuildersModule::class,
        ViewModelModule::class
    ]
)
object RepositoryModule {

    @Provides
    @JvmStatic
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    @Provides
    @JvmStatic
    @Singleton
    fun provideOkHttp(
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(BuildConfig.BASE_URL)
            addConverterFactory(GsonConverterFactory.create(gson))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(okHttpClient)
        }.build()
    }

    @Provides
    @JvmStatic
    @Singleton
    fun providePolaApi(retrofit: Retrofit): PolaApi = retrofit.create(PolaApi::class.java)

    @Provides
    @JvmStatic
    @Singleton
    fun providePermissionHandler(): PermissionHandler = PermissionHandlerImpl()

    @Provides
    @JvmStatic
    @Singleton
    fun provideSessionId(context: Context): SessionId = SessionId(context)

}