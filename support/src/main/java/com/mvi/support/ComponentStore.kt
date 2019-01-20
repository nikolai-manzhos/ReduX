package com.mvi.support

import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State

internal object ComponentStore {
    private val cache = mutableMapOf<String, Component<*, *>>()

    fun store(tag: String, component: Component<*, *>) = cache.put(tag, component)

    fun clear(tag: String) = cache.remove(tag)

    fun <A : Action, S : State> retrieve(tag: String): Component<A, S>? = cache[tag] as? Component<A, S>?
}
