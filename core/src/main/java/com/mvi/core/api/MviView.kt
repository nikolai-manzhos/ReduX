package com.mvi.core.api

import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import io.reactivex.Observable

interface MviView<A : Action, S : State> {
    val actions: Observable<A>
    fun render(state: S)
    fun provideComponent(): Component<A, S>
    fun provideNavigator(): Navigator<A>?
}
