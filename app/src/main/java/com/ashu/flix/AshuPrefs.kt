package com.ashu.flix

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/** Stores user name, DP, wallpapers and recent videos in private app storage. */
object AshuPrefs {
    private const val FILE = "ashu_flix_prefs"
    private const val KEY_NAME = "user_name"
    private const val KEY_RECENT = "recent"

    const val KIND_DP = "dp"
    const val KIND_WALLPAPER = "wallpaper"
    const val KIND_SPLASH = "splash"

    private fun prefs(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun getName(c: Context): String? =
        prefs(c).getString(KEY_NAME, null)?.takeIf { it.isNotBlank() }

    fun setName(c: Context, name: String) {
        prefs(c).edit().putString(KEY_NAME, name.trim()).apply()
    }

    // ---- images ----
    private fun imageFile(c: Context, kind: String) = File(c.filesDir, "ashu_$kind.jpg")

    fun hasImage(c: Context, kind: String) = imageFile(c, kind).exists()

    fun removeImage(c: Context, kind: String) {
        imageFile(c, kind).delete()
    }

    fun saveImage(c: Context, uri: Uri, kind: String): Boolean {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            c.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val maxSide = if (kind == KIND_DP) 768 else 1920
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > maxSide * 2) sample *= 2
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = c.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opts)
            } ?: return false
            FileOutputStream(imageFile(c, kind)).use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            true
        } catch (_: Throwable) {
            false
        }
    }

    fun loadBitmap(c: Context, kind: String): Bitmap? {
        val f = imageFile(c, kind)
        if (!f.exists()) return null
        return try { BitmapFactory.decodeFile(f.absolutePath) } catch (_: Throwable) { null }
    }

    fun toGrayscale(src: Bitmap): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        Canvas(out).drawBitmap(src, 0f, 0f, paint)
        return out
    }

    private fun centerCrop(src: Bitmap, w: Int, h: Int): Bitmap {
        val scale = maxOf(w.toFloat() / src.width, h.toFloat() / src.height)
        val sw = (w / scale).toInt().coerceIn(1, src.width)
        val sh = (h / scale).toInt().coerceIn(1, src.height)
        val x = (src.width - sw) / 2
        val y = (src.height - sh) / 2
        return Bitmap.createScaledBitmap(Bitmap.createBitmap(src, x, y, sw, sh), w, h, true)
    }

    /** Sets the user's wallpaper (dimmed) as the window background, or plain black. */
    fun applyWallpaper(activity: Activity) {
        val bmp = loadBitmap(activity, KIND_WALLPAPER)
        if (bmp == null) {
            activity.window.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            return
        }
        val dm = activity.resources.displayMetrics
        val cropped = centerCrop(bmp, dm.widthPixels, dm.heightPixels)
        val layers: Array<Drawable> = arrayOf(
            BitmapDrawable(activity.resources, cropped),
            ColorDrawable(Color.argb(140, 0, 0, 0))
        )
        activity.window.setBackgroundDrawable(LayerDrawable(layers))
    }

    // ---- recent videos ----
    fun addRecent(c: Context, title: String, uri: String) {
        val list = getRecents(c).filter { it.second != uri }.toMutableList()
        list.add(0, title to uri)
        val arr = JSONArray()
        list.take(10).forEach { arr.put(JSONObject().put("t", it.first).put("u", it.second)) }
        prefs(c).edit().putString(KEY_RECENT, arr.toString()).apply()
    }

    fun getRecents(c: Context): List<Pair<String, String>> {
        val raw = prefs(c).getString(KEY_RECENT, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { arr.getJSONObject(it).let { o -> o.getString("t") to o.getString("u") } }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    fun clearRecents(c: Context) {
        prefs(c).edit().remove(KEY_RECENT).apply()
    }
}
