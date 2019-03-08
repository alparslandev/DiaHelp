package com.diahelp.login

import android.os.Bundle
import com.diahelp.base.BaseActivity
import com.diahelp.R

class LoginActivity : BaseActivity<LoginContract.View, LoginContract.Presenter>(), LoginContract.View{


    override fun showLoading(visibility: Int) {

    }

    override var mPresenter: LoginContract.Presenter = LoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        mPresenter.loginWithCredientials("", "")
    }
}