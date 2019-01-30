package com.mvi.core

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

interface Action
interface State

interface MviView<A : Action, in S : State> {
    val actions: Observable<A>
    fun render(state: S)
}

interface Reducer<A : Action, S : State> {
    fun reduce(action: A, state: S): S
}

interface Middleware<A : Action, S : State> {
    fun intercept(actions: Observable<A>, state: Observable<S>): Observable<A>
}

class Component<A : Action, out S : State>(
    private val reducer: Reducer<A, S>,
    private val middlewares: Collection<Middleware<A, S>>,
    private val uiScheduler: Scheduler,
    initialState: S
) {
    private val state: BehaviorRelay<S> = BehaviorRelay.createDefault(initialState)
    private val actions: PublishRelay<A> = PublishRelay.create()

    fun connect(): Disposable {
        val disposable = CompositeDisposable()

        disposable += actions.withLatestFrom(state, reducer::reduce)
            .distinctUntilChanged()
            .subscribe(state::accept)

        disposable += Observable.merge(middlewares.map { middleware -> middleware.intercept(actions, state) })
            .subscribe(actions::accept)

        return disposable
    }

    fun bind(view: MviView<A, S>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state
            .observeOn(uiScheduler)
            .subscribe(view::render)

        disposable += view.actions.subscribe(actions::accept)
        return disposable
    }
}
