package pl.pola_app.utils

import android.app.Dialog
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import com.google.android.material.R

fun Dialog.applyWindowInsets() {
    if (this.window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val window = this.window
        window!!.findViewById<FrameLayout>(R.id.container).fitsSystemWindows = false
        val decorView = window.decorView
        decorView.systemUiVisibility =
            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding) -> Unit) {
    val initialPadding = recordInitialPaddingForView(this)
    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding)
        insets
    }
}

data class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) =
    InitialPadding(
        view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
    )