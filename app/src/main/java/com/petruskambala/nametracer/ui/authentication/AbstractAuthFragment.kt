package com.petruskambala.nametracer.ui.authentication

import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.petruskambala.nametracer.MainActivity
import com.petruskambala.nametracer.ui.AbstractFragment
import kotlinx.android.synthetic.main.activity_main.*

abstract class AbstractAuthFragment : AbstractFragment() {


    protected fun verifyEmail() {
        showProgressBar("Sending verification link...")
        accountModel.sendVerificationEmail()
    }

    protected fun isEmailVerified(): Boolean {
        return Firebase.auth.currentUser?.isEmailVerified == true
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}