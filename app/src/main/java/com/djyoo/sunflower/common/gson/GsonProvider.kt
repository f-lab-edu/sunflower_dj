package com.djyoo.sunflower.common.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory

/**
+ 앱 전체에서 공통으로 사용하는 [Gson] 인스턴스와
+ Retrofit용 [GsonConverterFactory]를 제공하는 프로바이더.
 */
object GsonProvider {

    /** 공통 Gson 인스턴스 */
    val gson: Gson = GsonBuilder()
        // 필요 시 공통 설정을 여기에서 추가한다.
        .create()

    /** Retrofit 에서 사용할 GsonConverterFactory */
    val gsonConverterFactory: GsonConverterFactory =
        GsonConverterFactory.create(gson)
}

