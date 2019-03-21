package com.mvi.core.api

import com.mvi.core.Action

interface Navigator<in A : Action> {
    fun navigate(action: A)
}
