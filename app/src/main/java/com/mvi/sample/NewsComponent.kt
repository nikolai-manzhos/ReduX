package com.mvi.sample

import com.mvi.core.Middleware
import com.mvi.core.Reducer
import com.mvi.sample.NewsAction.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException


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
