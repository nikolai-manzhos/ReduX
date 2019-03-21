package com.mvi.sample.detail

import com.mvi.core.api.Reducer

class DetailReducer : Reducer<DetailAction, DetailState> {
    override fun reduce(action: DetailAction, state: DetailState): DetailState {
        return state
    }
}
