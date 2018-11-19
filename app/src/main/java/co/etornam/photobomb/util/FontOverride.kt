package co.etornam.photobomb.util

import android.content.Context
import android.graphics.Typeface
import android.util.Log

object FontOverride {
    fun setDefaultFont(context: Context,
                       staticTypefaceFieldName: String, fontAssetName: String) {
        val regular = Typeface.createFromAsset(context.assets,
                fontAssetName)
        replaceFont(staticTypefaceFieldName, regular)
    }

    private fun replaceFont(staticTypefaceFieldName: String,
                            newTypeface: Typeface) {
        try {
            val staticField = Typeface::class.java
                    .getDeclaredField(staticTypefaceFieldName)
            staticField.isAccessible = true
            staticField.set(null, newTypeface)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            Log.d("FontOverride", "replaceFont: $e")
        }

    }
}
