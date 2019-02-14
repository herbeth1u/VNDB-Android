package com.booboot.vndbandroid.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.booboot.vndbandroid.R
import com.booboot.vndbandroid.model.vndbandroid.Preferences

inline fun <reified A : Activity> Context.startActivity(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(configIntent))
}

/**
 * Sets the light status and navigation bar when in Day mode.
 * Resets the status and navigation to default (dark) when in Night mode.
 * Cannot use conditional styles for this as there is an Android bug: when switching from Day to Night mode,
 * Android isn't able to reset these flags. Hence the programmatical reset.
 */
fun Activity.setLightStatusAndNavigation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_UNDEFINED, Configuration.UI_MODE_NIGHT_NO -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> 0
        }
    }
}

fun Context.getBitmap(@DrawableRes drawableRes: Int): Bitmap? {
    return VectorDrawableCompat.create(resources, drawableRes, theme)?.let {
        val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        it.setBounds(0, 0, canvas.width, canvas.height)
        it.draw(canvas)
        bitmap
    }
}

fun Context.openURL(url: String) {
    if (Preferences.useCustomTabs) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(getThemeColor(R.attr.colorPrimary))
        getBitmap(R.drawable.ic_arrow_back_white_24dp)?.let {
            builder.setCloseButtonIcon(it)
        }
        builder.setStartAnimations(this, R.anim.slide_in, R.anim.slide_out)
        builder.setExitAnimations(this, R.anim.slide_back_in, R.anim.slide_back_out)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    } else {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}

fun Context.getThemeColor(resid: Int): Int {
    val colorAttribute = TypedValue()
    theme.resolveAttribute(resid, colorAttribute, true)
    return colorAttribute.data
}

fun Context.getThemeColorState(@AttrRes resid: Int): ColorStateList? {
    val data = getThemeColor(resid)
    val arr = obtainStyledAttributes(data, intArrayOf(resid))
    val color = arr.getColorStateList(0)
    arr.recycle()
    return color
}

@ColorInt
fun Context.getThemeColorStateEnabled(@AttrRes resid: Int): Int {
    return getThemeColorState(resid)?.getColorForState(IntArray(android.R.attr.state_selected), -1) ?: -1
}

fun Context.dayNightTheme(): String {
    val themeName = TypedValue()
    theme.resolveAttribute(R.attr.themeName, themeName, true)
    return themeName.string.toString()
}