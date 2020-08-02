package com.petruskambala.namcovidcontacttracer.ui.authentication

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment

abstract class AbstractAuthFragment : AbstractFragment() {


    protected fun verifyEmail() {
        showProgressBar("Sending verification link...")
        accountModel.sendVerificationEmail()
    }

    protected fun isEmailVerified(): Boolean {
        return Firebase.auth.currentUser?.isEmailVerified == true
    }
}