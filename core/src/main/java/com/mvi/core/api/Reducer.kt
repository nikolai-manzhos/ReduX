package com.mvi.core.api

import com.mvi.core.Action
import com.mvi.core.State

interface Reducer<A : Action, S : State> {
    fun reduce(action: A, state: S): S
}
