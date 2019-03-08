package com.e.diahelp.base

open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {

    protected  var mView : V? = null

    override fun attachView(view: V) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }
}