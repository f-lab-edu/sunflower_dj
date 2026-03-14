package com.djyoo.sunflower.common.network

import android.util.Log
import com.djyoo.sunflower.common.network.RetrofitProvider.LOG_TAG
import com.djyoo.sunflower.common.network.RetrofitProvider.create
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API 추가 시 공통으로 사용하는 Retrofit 인스턴스 생성 유틸리티.
 * Base URL 상수와 [create] 메서드를 제공한다.
 * OkHttp [HttpLoggingInterceptor]를 Level.BODY로 적용해 요청/응답 URL과 본문을 Logcat에 출력한다(헤더 필드만 제외).
 *
 * 사용 예: `RetrofitProvider.create().create(UnsplashApi::class.java)`
 */
object RetrofitProvider {

    private const val LOG_TAG = "OkHttp"

    private const val BASE_URL = "https://api.unsplash.com/"

    /** 요청/응답 헤더 라인 패턴 (헤더는 로그에서 제외) */
    private val headerLineRegex = Regex("^[A-Za-z][A-Za-z0-9-]*:\\s.*")

    private val gson = GsonBuilder().create()

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        message.lineSequence()
            .filterNot { line -> headerLineRegex.matches(line.trim()) }
            .forEach { line -> if (line.isNotBlank()) Log.d(LOG_TAG, line) }
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Retrofit 인스턴스를 생성한다.
     * 요청/응답 URL과 본문이 Logcat 태그 [LOG_TAG]로 출력된다(헤더 필드만 제외).
     */
    fun create(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
