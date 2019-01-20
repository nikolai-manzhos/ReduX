package com.mvi.support

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.MviView
import com.mvi.core.State
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

private const val EXTRA_COMPONENT_ID = "EXTRA_COMPONENT_ID"

abstract class MviFragment<A : Action, S : State> : Fragment(), MviView<A, S> {

    private var connectionDisposable: Disposable? = null
    private var bindingDisposable: Disposable? = null

    // TODO find a better way to generate unique component id
    private var componentTag = UUID.randomUUID().toString()
    private val component: Component<A, S> by lazy(NONE) {
        val cachedComponent: Component<A, S>? = ComponentStore.retrieve(componentTag)
        return@lazy cachedComponent ?: provideComponent().also { ComponentStore.store(componentTag, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_COMPONENT_ID, componentTag)
    }

    override fun onResume() {
        super.onResume()
        bindingDisposable = component.bind(this)
    }

    override fun onPause() {
        super.onPause()
        bindingDisposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        val activity = requireActivity()
        if (activity.isFinishing) {
            connectionDisposable?.dispose()
            ComponentStore.clear(componentTag)
        }
    }

    abstract fun provideComponent(): Component<A, S>
    @LayoutRes abstract fun provideLayoutId(): Int

    private fun initComponent(savedState: Bundle?) {
        savedState?.apply { componentTag = getString(EXTRA_COMPONENT_ID)!! }
        connectionDisposable = component.connect()
    }

}