package com.ashu.flix

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast

/** Settings: name, profile photo, app wallpaper, splash wallpaper, About. */
class SettingsActivity : Activity() {
    private lateinit var list: LinearLayout
    private var pendingKind = AshuPrefs.KIND_DP
    private val reqImage = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        val pad = Ui.dp(this, 16f)
        list.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.fitsSystemWindows = true
        scroll.addView(list)
        setContentView(scroll)
    }

    override fun onResume() {
        super.onResume()
        AshuPrefs.applyWallpaper(this)
        render()
    }

    private fun lp(top: Float): LinearLayout.LayoutParams {
        val p = LinearLayout.LayoutParams(-1, -2)
        p.topMargin = Ui.dp(this, top)
        return p
    }

    private fun row(title: String, summary: String, onClick: () -> Unit): View {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        val p = Ui.dp(this, 16f)
        box.setPadding(p, p, p, p)
        box.background = Ui.card(this)
        box.addView(Ui.text(this, title, 16f, Color.WHITE, bold = true))
        box.addView(Ui.text(this, summary, 12f, Color.rgb(175, 175, 175)))
        box.setOnClickListener { onClick() }
        return box
    }

    private fun render() {
        list.removeAllViews()
        val back = Ui.text(this, "←  Settings", 22f, Color.WHITE, bold = true)
        back.setOnClickListener { finish() }
        list.addView(back)

        list.addView(row("Profile", AshuPrefs.getName(this) ?: "Naam set nahi hai") { nameDialog() }, lp(18f))
        list.addView(row("Profile photo (DP)",
            if (AshuPrefs.hasImage(this, AshuPrefs.KIND_DP)) "Custom photo set" else "Default") {
            imageChooser(AshuPrefs.KIND_DP, "Profile photo")
        }, lp(10f))
        list.addView(row("App wallpaper",
            if (AshuPrefs.hasImage(this, AshuPrefs.KIND_WALLPAPER)) "Custom wallpaper set" else "Default (black)") {
            imageChooser(AshuPrefs.KIND_WALLPAPER, "App wallpaper")
        }, lp(10f))
        list.addView(row("Splash screen wallpaper",
            if (AshuPrefs.hasImage(this, AshuPrefs.KIND_SPLASH)) "Custom wallpaper set" else "Default (black)") {
            imageChooser(AshuPrefs.KIND_SPLASH, "Splash wallpaper")
        }, lp(10f))
        list.addView(row("About", "Developer aur app ki details") {
            startActivity(Intent(this, AboutActivity::class.java))
        }, lp(10f))
    }

    private fun nameDialog() {
        val input = EditText(this)
        input.setText(AshuPrefs.getName(this) ?: "")
        input.setSingleLine(true)
        AlertDialog.Builder(this)
            .setTitle("Apna naam")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val n = input.text.toString().trim()
                if (n.isNotEmpty()) {
                    AshuPrefs.setName(this, n)
                    render()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun imageChooser(kind: String, title: String) {
        val options = arrayOf("Image choose karo", "Remove / default")
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(options) { _, which ->
                if (which == 0) {
                    pendingKind = kind
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    @Suppress("DEPRECATION")
                    startActivityForResult(i, reqImage)
                } else {
                    AshuPrefs.removeImage(this, kind)
                    AshuPrefs.applyWallpaper(this)
                    render()
                }
            }
            .show()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != reqImage || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        if (AshuPrefs.saveImage(this, uri, pendingKind)) {
            AshuPrefs.applyWallpaper(this)
            render()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image load nahi hui", Toast.LENGTH_SHORT).show()
        }
    }
}
