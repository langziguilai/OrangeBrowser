package com.dev.browser.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dev.browser.support.log.Logger

/**
 * Checks whether the given [test] byte sequence exists at the [offset] of this [ByteArray]
 */
fun ByteArray.containsAtOffset(offset: Int, test: ByteArray): Boolean {
    if (size - offset < test.size) {
        return false
    }

    for (i in 0 until test.size) {
        if (this[offset + i] != test[i]) {
            return false
        }
    }

    return true
}

fun ByteArray.toBitmap(opts: BitmapFactory.Options? = null): Bitmap? {
    return toBitmap(0, size, opts)
}

fun ByteArray.toBitmap(
    offset: Int,
    length: Int,
    opts: BitmapFactory.Options? = null
): Bitmap? {
    if (length <= 0) {
        return null
    }

    return try {
        val bitmap = BitmapFactory.decodeByteArray(this, offset, length, opts)

        if (bitmap == null) {
            null
        } else if (bitmap.width <= 0 || bitmap.height <= 0) {
            Logger.warn("Decoded bitmap jas dimensions: ${bitmap.width} x ${bitmap.height}")
            null
        } else {
            bitmap
        }
    } catch (e: OutOfMemoryError) {
        Logger.warn("OutOfMemoryError while decoding byte array", e)
        null
    }
}
