package com.diahelp.base

interface BasePresenter <in V : BaseView> {
    fun attachView(view : V)
    fun detachView()
}