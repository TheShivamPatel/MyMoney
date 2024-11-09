package com.mymoney.android.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun openBrowser(context: Context, url: String) {
    var finalUrl = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        finalUrl = "https://$url"
    }

    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
    context.startActivity(browserIntent)
}

fun openAppOnPlayStore(context: Context, appUrl: String) {
    try {
        val viewIntent = Intent(
            "android.intent.action.VIEW",
            Uri.parse("https://play.google.com/store/apps/details?id=$appUrl")
        )
        context.startActivity(viewIntent)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun shareImageFromDrawable(context: Context, drawableId: Int, text: String) {
    val drawable: Drawable? = ContextCompat.getDrawable(context, drawableId)
    if (drawable is BitmapDrawable) {
        val bitmap: Bitmap = drawable.bitmap

        val file = File(context.cacheDir, "shared_image.png")
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        val uri: Uri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    } else {
        Log.e("ShareImage", "Drawable is not a BitmapDrawable")
    }
}