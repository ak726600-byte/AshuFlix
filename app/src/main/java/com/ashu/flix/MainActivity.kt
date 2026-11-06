package com.ashu.flix

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/** Home: greeting with DP, play a local video or a link, recent list. */
class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private val reqVideo = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        val pad = Ui.dp(this, 16f)
        content.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.fitsSystemWindows = true
        scroll.addView(content)
        setContentView(scroll)
    }

    override fun onResume() {
        super.onResume()
        AshuPrefs.applyWallpaper(this)
        render()
    }

    private fun render() {
        content.removeAllViews()
        val name = AshuPrefs.getName(this) ?: "Friend"

        // header: DP + greeting + settings
        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL

        val size = Ui.dp(this, 56f)
        val dpBmp = AshuPrefs.loadBitmap(this, AshuPrefs.KIND_DP)
        val avatar: View = if (dpBmp != null) {
            val iv = ImageView(this)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.setImageBitmap(dpBmp)
            iv
        } else {
            val tv = Ui.text(this, name.take(1).uppercase(), 24f, Color.BLACK, bold = true, center = true)
            tv.setBackgroundColor(Color.WHITE)
            tv
        }
        Ui.makeCircle(avatar)
        header.addView(avatar, LinearLayout.LayoutParams(size, size))

        val greet = LinearLayout(this)
        greet.orientation = LinearLayout.VERTICAL
        greet.addView(Ui.text(this, "Hi, $name", 22f, Color.WHITE, bold = true))
        greet.addView(Ui.text(this, "Aaj kya dekhna hai?", 13f, Color.rgb(190, 190, 190)))
        val greetLp = LinearLayout.LayoutParams(0, -2, 1f)
        greetLp.leftMargin = Ui.dp(this, 14f)
        header.addView(greet, greetLp)

        val gear = Ui.text(this, "⚙", 28f, Color.WHITE, center = true)
        gear.setPadding(Ui.dp(this, 10f), Ui.dp(this, 4f), Ui.dp(this, 10f), Ui.dp(this, 4f))
        gear.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        header.addView(gear)
        content.addView(header)

        // actions
        content.addView(actionCard("📁", "Phone se video", "Apne phone ka koi bhi video chalao") { pickVideo() },
            topMarginParams(24f))
        content.addView(actionCard("🔗", "Link se chalao", "Direct video link (mp4, m3u8) paste karo") { linkDialog() },
            topMarginParams(12f))

        // recent
        val recents = AshuPrefs.getRecents(this)
        if (recents.isNotEmpty()) {
            val head = LinearLayout(this)
            head.orientation = LinearLayout.HORIZONTAL
            head.gravity = Gravity.CENTER_VERTICAL
            head.addView(Ui.text(this, "Recent", 18f, Color.WHITE, bold = true),
                LinearLayout.LayoutParams(0, -2, 1f))
            val clear = Ui.text(this, "Clear", 13f, Color.rgb(190, 190, 190))
            clear.setOnClickListener { AshuPrefs.clearRecents(this); render() }
            head.addView(clear)
            content.addView(head, topMarginParams(28f))
            for ((title, uri) in recents) {
                content.addView(actionCard("▶", title, "", compact = true) { play(uri, title) },
                    topMarginParams(10f))
            }
        }
    }

    private fun topMarginParams(dpTop: Float): LinearLayout.LayoutParams {
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.topMargin = Ui.dp(this, dpTop)
        return lp
    }

    private fun actionCard(
        icon: String, title: String, subtitle: String,
        compact: Boolean = false, onClick: () -> Unit
    ): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        val p = Ui.dp(this, if (compact) 12f else 18f)
        row.setPadding(p, p, p, p)
        row.background = Ui.card(this)
        row.addView(Ui.text(this, icon, if (compact) 18f else 26f, Color.WHITE))
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        val t = Ui.text(this, title, if (compact) 14f else 17f, Color.WHITE, bold = !compact)
        t.maxLines = 1
        t.ellipsize = android.text.TextUtils.TruncateAt.END
        col.addView(t)
        if (subtitle.isNotEmpty()) col.addView(Ui.text(this, subtitle, 12f, Color.rgb(170, 170, 170)))
        val colLp = LinearLayout.LayoutParams(0, -2, 1f)
        colLp.leftMargin = Ui.dp(this, 14f)
        row.addView(col, colLp)
        row.setOnClickListener { onClick() }
        return row
    }

    private fun pickVideo() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "video/*"
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        @Suppress("DEPRECATION")
        startActivityForResult(i, reqVideo)
    }

    private fun linkDialog() {
        val input = EditText(this)
        input.hint = "https://..."
        input.setSingleLine(true)
        AlertDialog.Builder(this)
            .setTitle("Video link")
            .setView(input)
            .setPositiveButton("Play") { _, _ ->
                val url = input.text.toString().trim()
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    val title = url.substringAfterLast('/').substringBefore('?').ifBlank { url }
                    AshuPrefs.addRecent(this, title, url)
                    play(url, title)
                } else {
                    Toast.makeText(this, "Valid http/https link daalo", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun play(uri: String, title: String) {
        val i = Intent(this, PlayerActivity::class.java)
        i.putExtra("uri", uri)
        i.putExtra("title", title)
        startActivity(i)
    }

    private fun displayName(uri: Uri): String {
        try {
            contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && c.moveToFirst()) return c.getString(idx)
            }
        } catch (_: Throwable) {
        }
        return uri.lastPathSegment ?: "Video"
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != reqVideo || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Throwable) {
        }
        val title = displayName(uri)
        AshuPrefs.addRecent(this, title, uri.toString())
        play(uri.toString(), title)
    }
}
