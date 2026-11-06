package com.ashu.flix

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView

object Ui {
    fun dp(c: Context, v: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v, c.resources.displayMetrics
    ).toInt()

    fun text(
        c: Context, t: String, sp: Float, color: Int,
        bold: Boolean = false, center: Boolean = false
    ): TextView = TextView(c).apply {
        text = t
        setTextColor(color)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        if (bold) typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        if (center) gravity = Gravity.CENTER
    }

    /** Dark translucent card with a thin white border (readable over any wallpaper). */
    fun card(c: Context, radiusDp: Float = 16f, fill: Int = Color.argb(170, 0, 0, 0)) =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(c, radiusDp).toFloat()
            setStroke(dp(c, 1f), Color.argb(120, 255, 255, 255))
        }

    fun makeCircle(v: View) {
        v.clipToOutline = true
        v.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }
    }
}
