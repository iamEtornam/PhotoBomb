package co.etornam.photobomb

import android.app.Application

import co.etornam.photobomb.util.FontOverride

class PhotoBomb : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread(Runnable {
            FontOverride.setDefaultFont(this@PhotoBomb, "DEFAULT", "Comfortaa-Light.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "MONOSPACE", "Comfortaa-Regular.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "SERIF", "Comfortaa-Bold.ttf")
            FontOverride.setDefaultFont(this@PhotoBomb, "SANS_SERIF", "Comfortaa-Bold.ttf")
        }).start()
    }
}
