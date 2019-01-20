package com.mvi.sample

import com.mvi.core.Middleware
import okhttp3.OkHttpClient

object Injection {
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    private fun provideApi(): Api {
        return Api(provideOkHttpClient())
    }

    fun provideNewsMiddleware(): Collection<Middleware<NewsAction, NewsState>> {
        return listOf(NewsMiddleware(provideApi()))
    }
}
