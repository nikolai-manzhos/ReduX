package com.mvi.sample

import com.mvi.core.api.Middleware
import com.mvi.sample.news.NewsAction
import com.mvi.sample.news.NewsMiddleware
import com.mvi.sample.news.NewsNavigatorMiddleware
import com.mvi.sample.news.NewsState
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
        return listOf(NewsMiddleware(provideApi()), NewsNavigatorMiddleware())
    }
}
