package com.mvi.sample.news

import android.support.v4.app.FragmentActivity
import com.mvi.core.api.Middleware
import com.mvi.core.api.Navigator
import com.mvi.core.api.Reducer
import com.mvi.sample.Api
import com.mvi.sample.R
import com.mvi.sample.detail.ArticleDetailFragment
import com.mvi.sample.news.NewsAction.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class NewsNavigator(private val activity: FragmentActivity) : Navigator<NewsAction> {
    override fun navigate(action: NewsAction) {
        when (action) {
            NewsAction.ArticleClickAction -> {
                val fragmentManager = activity.supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.content, ArticleDetailFragment())
                    .addToBackStack(null)
                    .commit()
            }
            else -> { /*ignore*/ }
        }
    }
}


class NewsReducer : Reducer<NewsAction, NewsState> {
    override fun reduce(action: NewsAction, state: NewsState): NewsState {
        return when(action) {
            is NewsLoadingAction -> NewsState.LoadingState
            is NewsSuccessAction -> NewsState.NewsLoadedState(action.headLines)
            is NewsFailureAction -> NewsState.NewsFailureState(action.exception)
            else -> state
        }
    }
}

class NewsMiddleware(private val api: Api) : Middleware<NewsAction, NewsState> {
    override fun intercept(actions: Observable<NewsAction>, state: Observable<NewsState>): Observable<NewsAction> {
        return actions.ofType(InitialLoadAction.javaClass)
            .switchMap<NewsAction> {
                api.requestTopHeadlines()
                    .subscribeOn(Schedulers.io())
                    .map(::NewsSuccessAction)
            }.onErrorReturn(::NewsFailureAction)
    }
}

class NewsNavigatorMiddleware : Middleware<NewsAction, NewsState> {
    override fun intercept(actions: Observable<NewsAction>, state: Observable<NewsState>): Observable<NewsAction> {
        return actions.ofType(NewsAction.ArticleClickAction.javaClass)
            .switchMap<NewsAction> { Observable.empty() }
    }
}
