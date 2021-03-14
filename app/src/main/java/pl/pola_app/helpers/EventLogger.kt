package pl.pola_app.helpers

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import pl.pola_app.BuildConfig

open class EventLogger(private val c: Context) {
    fun logSearch(result: String, deviceId: String?, source: String) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        val bundle = Bundle()
        bundle.putString("code", result)
        deviceId?.run {
            bundle.putString("device_id", this)
        }

        bundle.putString("source", source)
        FirebaseAnalytics.getInstance(c).logEvent("scan_code", bundle)
    }

    fun logCustom(eventName: String, bundle: Bundle) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        FirebaseAnalytics.getInstance(c).logEvent(eventName, bundle)
    }

    fun logContentView(
        contentName: String,
        contentType: String,
        contentId: String,
        code: String?,
        deviceId: String?
    ) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        val bundle = Bundle()
        bundle.putString("company", contentName)
        deviceId?.run {
            bundle.putString("device_id", this)
        }
        bundle.putString("product_id", contentId)
        code?.run {
            bundle.putString("code", this)
        }
        FirebaseAnalytics.getInstance(c).logEvent(contentType, bundle)
    }

    fun logException(throwable: Throwable) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        try {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logLevelStart(levelName: String, productId: String, deviceId: String) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        val bundle = Bundle()
        bundle.putString("device_id", deviceId)
        bundle.putString("code", productId)
        FirebaseAnalytics.getInstance(c).logEvent(levelName + "_started", bundle)
    }

    fun logLevelEnd(levelName: String, productId: String, deviceId: String) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        val bundle = Bundle()
        bundle.putString("device_id", deviceId)
        bundle.putString("code", productId)
        FirebaseAnalytics.getInstance(c).logEvent(levelName + "_finished", bundle)
    }

    fun logMenuItemOpened(itemName: String, deviceId: String) {
        if (!BuildConfig.USE_FIREBASE) {
            return
        }
        val bundle = Bundle()
        bundle.putString("item", itemName)
        bundle.putString("device_id", deviceId)
        FirebaseAnalytics.getInstance(c).logEvent("menu_item_opened", bundle)
    }
}