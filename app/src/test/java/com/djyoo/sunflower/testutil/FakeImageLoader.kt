package com.djyoo.sunflower.testutil

import android.widget.ImageView
import com.djyoo.sunflower.common.image.ImageLoader

class FakeImageLoader : ImageLoader {
    private var onLoaded: (() -> Unit)? = null

    override fun load(url: String, imageView: ImageView, onLoaded: () -> Unit) {
        this.onLoaded = onLoaded
    }

    fun triggerLoaded() {
        onLoaded?.invoke()
    }
}
