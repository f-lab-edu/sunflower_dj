package com.djyoo.sunflower.common.base

import android.app.Activity
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes private val layoutResId: Int) : AppCompatActivity() {
    protected val binding: T by lazy { DataBindingUtil.setContentView(this, layoutResId) }
    protected lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+ SplashScreen API 및 compat 라이브러리 설치
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // edge-to-edge: status bar / navigation bar 영역까지 액티비티 배경이 보이도록
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding.lifecycleOwner = this
        activity = this

        // 시스템 바(status bar, navigation bar)만큼 루트 뷰에 padding 적용
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}