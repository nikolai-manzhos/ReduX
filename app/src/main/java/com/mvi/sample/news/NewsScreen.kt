package com.mvi.sample.news

import android.view.LayoutInflater
import com.jakewharton.rxrelay2.PublishRelay
import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import com.mvi.core.api.Navigator
import com.mvi.sample.*
import com.mvi.sample.news.NewsState.InitialState
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

    object ArticleClickAction : NewsAction()
}

sealed class NewsState : State {
    object InitialState : NewsState()
    object LoadingState : NewsState()
    class NewsLoadedState(val result: TopHeadLines) : NewsState()
    class NewsFailureState(val throwable: Throwable) : NewsState()
}


class NewsFragment : MviFragment<NewsAction, NewsState>() {

    // Clicks
    private val clickActions = PublishRelay.create<NewsAction>()
    override val actions: Observable<NewsAction> =
        Observable.merge(Observable.just(NewsAction.InitialLoadAction), clickActions)

    override fun render(state: NewsState) {
        return when (state) {
            InitialState -> {
                progress.gone()
            }
            NewsState.LoadingState -> {
                progress.visible()
            }
            is NewsState.NewsLoadedState -> {
                state.result.articles.forEach { article ->
                    LayoutInflater.from(context).inflate(R.layout.item_article, articlesContainer, false)
                        .apply { desc.text = article.description }
                        .also(articlesContainer::addView)
                        .apply {
                            setOnClickListener { clickActions.accept(NewsAction.ArticleClickAction) }
                        }
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

    override fun provideComponent(): Component<NewsAction, NewsState> =
        Component(
            NewsReducer(),
            Injection.provideNewsMiddleware(),
            AndroidSchedulers.mainThread(),
            InitialState
        )
    override fun provideNavigator(): Navigator<NewsAction> = NewsNavigator(requireActivity())
    override fun provideLayoutId(): Int = R.layout.fragment_news
}
