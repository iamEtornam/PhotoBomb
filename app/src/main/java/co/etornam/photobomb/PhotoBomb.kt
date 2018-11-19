package co.etornam.photobomb

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat

import co.etornam.photobomb.util.FontOverride

class PhotoBomb : Application() {
    override fun onCreate() {
        super.onCreate()
	    val config = BundledEmojiCompatConfig(this)
	    EmojiCompat.init(config)

        Thread(Runnable {
            FontOverride.setDefaultFont(this@PhotoBomb, "DEFAULT", "Comfortaa-Light.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "MONOSPACE", "Comfortaa-Regular.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "SERIF", "Comfortaa-Bold.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "SANS_SERIF", "Comfortaa-Bold.ttf")
        }).start()
    }
}
