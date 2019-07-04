package com.dev.base.extension

import android.content.res.Resources
import android.util.TypedValue
import com.dev.util.Keep

/**
 * Converts a value in density independent pixels (pxToDp) to the actual pixel values for the display.
 */
@Keep
fun Resources.pxToDp(pixels: Int) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, pixels.toFloat(), displayMetrics).toInt()
