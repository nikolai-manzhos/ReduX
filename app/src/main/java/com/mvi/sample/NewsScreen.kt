package com.mvi.sample

import android.view.LayoutInflater
import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import com.mvi.sample.NewsState.InitialState
import com.mvi.support.MviFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.item_article.view.*

sealed class NewsAction : Action {
    object InitialLoadAction : NewsAction()

    class NewsSuccessAction(val headLines: TopHeadLines) : NewsAction()
    class NewsFailureAction(val exception: Throwable) : NewsAction()
    object NewsLoadingAction : NewsAction()
}

sealed class NewsState : State {
    object InitialState : NewsState()
    object LoadingState : NewsState()
    class NewsLoadedState(val result: TopHeadLines) : NewsState()
    class NewsFailureState(val throwable: Throwable) : NewsState()
}


class NewsFragment : MviFragment<NewsAction, NewsState>() {
    override fun provideComponent(): Component<NewsAction, NewsState> =
        Component(NewsReducer(), Injection.provideNewsMiddleware(), AndroidSchedulers.mainThread(), InitialState)

    override val actions: Observable<NewsAction> = Observable.just(NewsAction.InitialLoadAction)
    override fun provideLayoutId(): Int = R.layout.fragment_news

    override fun render(state: NewsState) {
        return when (state) {
            InitialState -> {
                progress.gone()
            }
            NewsState.LoadingState -> {
                progress.visible()
            }
            is NewsState.NewsLoadedState -> {
                state.result.articles.forEach {
                    LayoutInflater.from(context).inflate(R.layout.item_article, articlesContainer, false)
                        .apply { desc.text = it.description }
                        .also(articlesContainer::addView)
                }
                progress.gone()
                retry.gone()
            }
            is NewsState.NewsFailureState -> {
                progress.gone()
                retry.visible()
            }
        }
    }

}