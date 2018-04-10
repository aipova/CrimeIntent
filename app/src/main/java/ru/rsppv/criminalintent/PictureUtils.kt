package ru.rsppv.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

class PictureUtils {

    companion object {
        fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap? {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, options)

            val srcWidth = options.outWidth
            val srcHeight = options.outHeight

            var inSampleSize = 1
            if (srcHeight > destHeight || srcWidth > destWidth) {
                val heightScale = srcHeight / destHeight
                val widthScale = srcWidth / destWidth
                inSampleSize =
                        Math.round((if (heightScale > widthScale) heightScale else widthScale).toDouble())
                            .toInt()
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            return BitmapFactory.decodeFile(path, options)
        }

        fun getScaledBitmap(path: String, activity: Activity): Bitmap? {
            val size = Point().apply {
                activity.windowManager.defaultDisplay.getSize(this)
            }
            return getScaledBitmap(path, size.x, size.y)
        }
    }
}