package com.ashu.flix

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/** Black & white animated "Ashu Flix" splash with optional (grayscale) wallpaper. */
class SplashActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.BLACK)

        AshuPrefs.loadBitmap(this, AshuPrefs.KIND_SPLASH)?.let { bmp ->
            val iv = ImageView(this)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.setImageBitmap(AshuPrefs.toGrayscale(bmp))
            iv.alpha = 0f
            iv.animate().alpha(0.45f).setDuration(900).start()
            root.addView(iv, FrameLayout.LayoutParams(-1, -1))
        }

        fun word(t: String, color: Int): TextView {
            val tv = Ui.text(this, t, 44f, color, bold = true)
            tv.letterSpacing = 0.04f
            tv.alpha = 0f
            tv.translationY = Ui.dp(this, 28f).toFloat()
            tv.scaleX = 0.9f
            tv.scaleY = 0.9f
            return tv
        }

        val ashu = word("Ashu", Color.WHITE)
        val flix = word(" Flix", Color.rgb(150, 150, 150))
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER
        row.addView(ashu)
        row.addView(flix)

        val line = View(this)
        line.setBackgroundColor(Color.WHITE)
        line.scaleX = 0f

        val tag = Ui.text(this, "STREAM  •  WATCH  •  ENJOY", 11f, Color.rgb(130, 130, 130), center = true)
        tag.letterSpacing = 0.3f
        tag.alpha = 0f

        val column = LinearLayout(this)
        column.orientation = LinearLayout.VERTICAL
        column.gravity = Gravity.CENTER_HORIZONTAL
        column.addView(row)
        val lineLp = LinearLayout.LayoutParams(Ui.dp(this, 120f), Ui.dp(this, 2f))
        lineLp.topMargin = Ui.dp(this, 10f)
        column.addView(line, lineLp)
        val tagLp = LinearLayout.LayoutParams(-2, -2)
        tagLp.topMargin = Ui.dp(this, 14f)
        column.addView(tag, tagLp)

        root.addView(column, FrameLayout.LayoutParams(-2, -2, Gravity.CENTER))
        setContentView(root)

        fun reveal(v: View, delay: Long) {
            v.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f)
                .setStartDelay(delay).setDuration(650)
                .setInterpolator(DecelerateInterpolator()).start()
        }
        reveal(ashu, 250)
        reveal(flix, 550)
        line.animate().scaleX(1f).setStartDelay(900).setDuration(600).start()
        tag.animate().alpha(1f).setStartDelay(1200).setDuration(500).start()

        handler.postDelayed({ column.animate().alpha(0f).setDuration(350).start() }, 2300)
        handler.postDelayed({ goNext() }, 2650)
    }

    private fun goNext() {
        val next = if (AshuPrefs.getName(this) == null) WelcomeActivity::class.java
        else MainActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
