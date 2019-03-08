package com.e.diahelp.login

import com.e.diahelp.base.BasePresenter
import com.e.diahelp.base.BaseView

object LoginContract {

    interface View : BaseView {
        fun showLoading(visibility : Int)
    }

    interface Presenter : BasePresenter<View> {
        fun loginWithCredientials(email : String, pass : String)
    }
}