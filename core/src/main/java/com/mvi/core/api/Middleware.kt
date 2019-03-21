package com.mvi.core.api

import com.mvi.core.Action
import com.mvi.core.State
import io.reactivex.Observable

interface Middleware<A : Action, S : State> {
    fun intercept(actions: Observable<A>, state: Observable<S>): Observable<A>
}
