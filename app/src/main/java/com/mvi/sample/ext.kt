package com.mvi.sample

import android.view.View

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}
