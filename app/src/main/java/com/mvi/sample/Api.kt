package com.mvi.sample

import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request

data class TopHeadLines(
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val description: String,
    val image: String
)

private const val API_KEY = "2295b5681975488c8867850936bff3c2"

class Api(private val okHttpClient: OkHttpClient) {

    private val gson = Gson()

    fun requestTopHeadlines(): Observable<TopHeadLines> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us&apiKey=$API_KEY")
            .build()
        val call = okHttpClient.newCall(request)
        return Observable.create { emitter ->
            try {
                val result = call.execute().body()?.string()
                val topHeadlines = gson.fromJson<TopHeadLines>(result, TopHeadLines::class.java)
                if (!emitter.isDisposed) {
                    emitter.onNext(topHeadlines)
                }

            } catch (t: Throwable) {
                if (!emitter.isDisposed) {
                    emitter.onError(t)
                }
            }
        }
    }
}