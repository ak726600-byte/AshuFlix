package com.ashu.flix

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView

/** About page with developer photo and details. */
class AboutActivity : Activity() {
    private val bg = Color.rgb(17, 16, 10)
    private val cardColor = Color.rgb(26, 24, 16)
    private val gold = Color.rgb(201, 186, 127)
    private val cream = Color.rgb(238, 232, 215)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(bg))

        val photo = ImageView(this)
        photo.setImageResource(R.drawable.ashu_photo)
        photo.scaleType = ImageView.ScaleType.CENTER_CROP
        Ui.makeCircle(photo)

        val bio = Ui.text(
            this,
            "Hey, I'm Ashutosh Kumar 👋\n\n" +
                "💻 App Developer | Future Entrepreneur 🚀\n" +
                "🧠 A curious mind with ambitious dreams.\n" +
                "✨ I love building things, learning new skills & turning ideas into reality.\n" +
                "🥂 I appreciate good conversations, genuine people & positive energy.\n" +
                "🖤 Quiet by nature, ambitious by mindset.\n" +
                "🌍 Just a guy creating his own story, one step at a time.",
            15f, Color.rgb(215, 210, 195), center = true
        )
        bio.setLineSpacing(Ui.dp(this, 4f).toFloat(), 1f)

        val insta = Ui.text(this, "@17__ashu_", 15f, gold, bold = true, center = true)
        val ip = Ui.dp(this, 14f)
        insta.setPadding(ip, ip, ip, ip)
        insta.background = GradientDrawable().apply {
            setColor(Color.rgb(66, 58, 30))
            cornerRadius = Ui.dp(this@AboutActivity, 30f).toFloat()
        }
        insta.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/17__ashu_")))
        }

        val inner = LinearLayout(this)
        inner.orientation = LinearLayout.VERTICAL
        inner.gravity = Gravity.CENTER_HORIZONTAL
        val pad = Ui.dp(this, 20f)
        inner.setPadding(pad, Ui.dp(this, 24f), pad, Ui.dp(this, 24f))
        inner.background = GradientDrawable().apply {
            setColor(cardColor)
            cornerRadius = Ui.dp(this@AboutActivity, 28f).toFloat()
        }
        inner.addView(Ui.text(this, "Developer", 16f, gold, bold = true))
        val photoLp = LinearLayout.LayoutParams(Ui.dp(this, 150f), Ui.dp(this, 150f))
        photoLp.topMargin = Ui.dp(this, 18f)
        inner.addView(photo, photoLp)
        val nameLp = LinearLayout.LayoutParams(-1, -2)
        nameLp.topMargin = Ui.dp(this, 18f)
        inner.addView(Ui.text(this, "Ashutosh Kumar", 26f, cream, bold = true, center = true), nameLp)
        val roleLp = LinearLayout.LayoutParams(-1, -2)
        roleLp.topMargin = Ui.dp(this, 4f)
        inner.addView(Ui.text(this, "Lead developer", 15f, gold, bold = true, center = true), roleLp)
        val bioLp = LinearLayout.LayoutParams(-1, -2)
        bioLp.topMargin = Ui.dp(this, 18f)
        inner.addView(bio, bioLp)
        val instaLp = LinearLayout.LayoutParams(-1, -2)
        instaLp.topMargin = Ui.dp(this, 20f)
        inner.addView(insta, instaLp)

        val column = LinearLayout(this)
        column.orientation = LinearLayout.VERTICAL
        val cp = Ui.dp(this, 16f)
        column.setPadding(cp, cp, cp, Ui.dp(this, 24f))
        val titleLp = LinearLayout.LayoutParams(-1, -2)
        titleLp.bottomMargin = Ui.dp(this, 14f)
        val title = Ui.text(this, "←  About", 24f, cream, bold = true)
        title.setOnClickListener { finish() }
        column.addView(title, titleLp)
        column.addView(inner, LinearLayout.LayoutParams(-1, -2))
        column.addView(
            Ui.text(this, "Ashu Flix v1.0", 12f, Color.rgb(140, 135, 120), center = true),
            LinearLayout.LayoutParams(-1, -2).apply { topMargin = Ui.dp(this@AboutActivity, 20f) }
        )

        val scroll = ScrollView(this)
        scroll.fitsSystemWindows = true
        scroll.addView(column)
        setContentView(scroll)
    }
}
