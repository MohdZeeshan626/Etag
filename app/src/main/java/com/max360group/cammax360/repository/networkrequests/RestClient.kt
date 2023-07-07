package com.max360group.cammax360.repository.networkrequests

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.max360group.cammax360.utils.ApplicationGlobal
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestClient {
    private var REST_CLIENT: API? = null
    var retrofitInstance: Retrofit? = null

    init {
        setUpRestClient()
    }

    fun get(): API {
        return REST_CLIENT!!
    }

    private fun setUpRestClient() {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            /* .addInterceptor(ChuckerInterceptor(ApplicationGlobal.context!!))*/
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("authorization", ApplicationGlobal.accessToken)
                    .addHeader("locale", ApplicationGlobal.deviceLocale)
                    .addHeader("accountId", ApplicationGlobal.accountId).build()
                chain.proceed(request)
            }
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        retrofitInstance = Retrofit.Builder()
            .baseUrl(WebConstants.ACTION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        REST_CLIENT = retrofitInstance!!.create(API::class.java)
    }
}