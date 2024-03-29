package com.mvi.support

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mvi.core.Action
import com.mvi.core.Component
import com.mvi.core.State
import com.mvi.core.api.MviView
import com.mvi.core.api.Navigator
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

private const val EXTRA_COMPONENT_ID = "EXTRA_COMPONENT_ID"

abstract class MviFragment<A : Action, S : State> : Fragment(), MviView<A, S> {

    private var connectionDisposable: Disposable? = null
    private var bindingDisposable: Disposable? = null

    private var componentTag = UUID.randomUUID().toString()
    private val component: Component<A, S> by lazy(NONE) {
        val cachedComponent: Component<A, S>? = ComponentStore.retrieve(componentTag)
        return@lazy cachedComponent ?: provideComponent().also { ComponentStore.store(componentTag, it) }
    }
    private val navigator: Navigator<A>? by lazy(NONE) { provideNavigator() }

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
        bindingDisposable = component.bind(this, navigator)
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

    @LayoutRes abstract fun provideLayoutId(): Int
    override fun provideNavigator(): Navigator<A>? = null

    private fun initComponent(savedState: Bundle?) {
        savedState?.apply { componentTag = getString(EXTRA_COMPONENT_ID)!! }
        connectionDisposable = component.connect()
    }
}
