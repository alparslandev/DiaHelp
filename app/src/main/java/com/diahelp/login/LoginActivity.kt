package com.diahelp.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.diahelp.MainActivity
import com.diahelp.R
import com.diahelp.base.BaseMvpActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseMvpActivity<LoginContract.View, LoginContract.Presenter>(), LoginContract.View {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun showLoading(visibility: Int) {
        pb_login.visibility = visibility
        btn_sign_in.isEnabled = visibility == View.GONE && chk_user_agreement.isChecked
    }

    override var mPresenter: LoginContract.Presenter = LoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_sign_in.isEnabled = false
        btn_sign_in.setOnClickListener { signIn() }
        mPresenter.loginWithCredientials("", "")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        val spannableString = SpannableString(
            resources.getString(R.string.user_agreement_text_prefix) + " " + resources.getString(R.string.user_agreement_text)
        )
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val userDialog = UserAgreementDialog(widget.context)
                userDialog.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        spannableString.setSpan(clickableSpan,0,
            resources.getString(R.string.user_agreement_text_prefix).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tv_user_agreement.text = spannableString
        tv_user_agreement.movementMethod = LinkMovementMethod.getInstance()
        tv_user_agreement.highlightColor = Color.TRANSPARENT

        chk_user_agreement.setOnCheckedChangeListener {_, isChecked -> btn_sign_in.isEnabled = isChecked }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        showLoading(View.VISIBLE)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
                showLoading(View.GONE)
            }
    }

    private fun signIn() {
        showLoading(View.VISIBLE)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
        }
    }

    private fun revokeAccess() {
        auth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        showLoading(View.GONE)
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}