package com.djyoo.sunflower.common.image

import android.widget.ImageView

interface ImageLoader {
    fun load(url: String, imageView: ImageView, onLoaded: () -> Unit)
}
