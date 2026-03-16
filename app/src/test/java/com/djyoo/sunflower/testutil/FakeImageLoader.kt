package com.djyoo.sunflower.testutil

import android.widget.ImageView
import com.djyoo.sunflower.common.image.ImageLoader

class FakeImageLoader : ImageLoader {
    private var callback: (() -> Unit)? = null
    private var loaded = false

    override fun load(url: String, imageView: ImageView, onLoaded: () -> Unit) {
        callback = onLoaded

        if (loaded) {
            onLoaded()
        }
    }

    fun triggerLoaded() {
        loaded = true
        callback?.invoke()
    }
}
