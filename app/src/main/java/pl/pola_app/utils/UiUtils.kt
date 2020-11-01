package pl.pola_app.utils

import android.content.res.Resources

fun Int.dpToPx() = this * Resources.getSystem().displayMetrics.density