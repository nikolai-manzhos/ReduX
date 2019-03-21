package com.mvi.sample.detail

import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import com.mvi.sample.R
import com.mvi.support.MviFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

sealed class DetailAction : Action {
    object InitialAction : DetailAction()
}

sealed class DetailState : State {
    object InitialState : DetailState()
}

class ArticleDetailFragment : MviFragment<DetailAction, DetailState>() {

    override fun provideComponent(): Component<DetailAction, DetailState> =
        Component(DetailReducer(), emptyList(), AndroidSchedulers.mainThread(), DetailState.InitialState)

    override fun provideLayoutId(): Int = R.layout.fragment_detail

    override val actions: Observable<DetailAction> = Observable.just(DetailAction.InitialAction)

    override fun render(state: DetailState) {
        // TODO render
    }


}