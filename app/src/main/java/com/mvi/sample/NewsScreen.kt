package com.mvi.sample

import android.view.View.GONE
import android.view.View.VISIBLE
import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import com.mvi.sample.NewsState.InitialState
import com.mvi.support.MviFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_news.*

sealed class NewsAction : Action {
    object InitialLoadAction : NewsAction()

    class NewsSuccessAction(val headLines: TopHeadLines) : NewsAction()
    class NewsFailureAction(val exception: Throwable) : NewsAction()
    object NewsLoadingAction : NewsAction()
}

sealed class NewsState : State {
    object InitialState : NewsState()
    object LoadingState : NewsState()
    class NewsLoadedState(private val headLine: TopHeadLines) : NewsState()
    class NewsFailureState(private val throwable: Throwable) : NewsState()
}


class NewsFragment : MviFragment<NewsAction, NewsState>() {
    override fun provideComponent(): Component<NewsAction, NewsState> =
        Component(NewsReducer(), Injection.provideNewsMiddleware(), AndroidSchedulers.mainThread(), InitialState)

    override val actions: Observable<NewsAction> = Observable.just(NewsAction.InitialLoadAction)
    override fun provideLayoutId(): Int = R.layout.fragment_news

    override fun render(state: NewsState) {
        return when (state) {
            InitialState -> {
                progress.visibility = GONE
                stateName.text = state.javaClass.simpleName
            }
            NewsState.LoadingState -> {
                progress.visibility = VISIBLE
                stateName.visibility = GONE
            }
            is NewsState.NewsLoadedState -> {
                progress.visibility = GONE
                stateName.text = state.javaClass.simpleName
                stateName.visibility = VISIBLE
            }
            is NewsState.NewsFailureState -> {
                progress.visibility = GONE
                stateName.text = state.javaClass.simpleName
                stateName.visibility = VISIBLE
            }
        }
    }

}