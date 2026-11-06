package com.ashu.flix

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

/** First-run welcome: the user sets their name. */
class WelcomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        val title = Ui.text(this, "Welcome to\nAshu Flix", 36f, Color.WHITE, bold = true, center = true)
        val sub = Ui.text(this, "Aapka naam kya hai?", 16f, Color.rgb(160, 160, 160), center = true)

        val input = EditText(this)
        input.hint = "Apna naam likho"
        input.setHintTextColor(Color.rgb(110, 110, 110))
        input.setTextColor(Color.WHITE)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.setSingleLine(true)
        input.gravity = Gravity.CENTER
        input.setPadding(Ui.dp(this, 16f), Ui.dp(this, 14f), Ui.dp(this, 16f), Ui.dp(this, 14f))
        input.background = GradientDrawable().apply {
            setStroke(Ui.dp(this@WelcomeActivity, 1.5f), Color.WHITE)
            cornerRadius = Ui.dp(this@WelcomeActivity, 14f).toFloat()
            setColor(Color.TRANSPARENT)
        }

        val button = Button(this)
        button.text = "Continue"
        button.isAllCaps = false
        button.setTextColor(Color.BLACK)
        button.textSize = 16f
        button.background = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = Ui.dp(this@WelcomeActivity, 14f).toFloat()
        }

        val column = LinearLayout(this)
        column.orientation = LinearLayout.VERTICAL
        column.gravity = Gravity.CENTER
        column.fitsSystemWindows = true
        val pad = Ui.dp(this, 28f)
        column.setPadding(pad, pad, pad, pad)
        column.addView(title)
        val subLp = LinearLayout.LayoutParams(-1, -2)
        subLp.topMargin = Ui.dp(this, 14f)
        subLp.bottomMargin = Ui.dp(this, 32f)
        column.addView(sub, subLp)
        column.addView(input, LinearLayout.LayoutParams(-1, -2))
        val btnLp = LinearLayout.LayoutParams(-1, Ui.dp(this, 52f))
        btnLp.topMargin = Ui.dp(this, 18f)
        column.addView(button, btnLp)
        setContentView(column)

        fun submit() {
            val name = input.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Pehle naam likho", Toast.LENGTH_SHORT).show()
                return
            }
            AshuPrefs.setName(this, name)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        button.setOnClickListener { submit() }
        input.setOnEditorActionListener { _, _, _ -> submit(); true }
    }
}
