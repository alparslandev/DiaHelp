package com.diahelp.login

import com.diahelp.base.BasePresenter
import com.diahelp.base.BaseView

object LoginContract {

    interface View : BaseView {
        fun showLoading(visibility : Int)
    }

    interface Presenter : BasePresenter<View> {
        fun loginWithCredientials(email : String, pass : String)
    }
}