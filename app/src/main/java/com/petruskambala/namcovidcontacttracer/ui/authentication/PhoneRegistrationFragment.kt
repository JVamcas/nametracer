package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.AuthType
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_email_registration.*

/**
 * A simple [Fragment] subclass.
 */
class PhoneRegistrationFragment : EmailRegistrationFragment() {

    override var authType: AuthType = AuthType.PHONE


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        new_account_btn.setOnClickListener {
            new_account_btn.isEnabled = false
            if (account.accountType == AccountType.BUSINESS) {
                val repoResults = accountModel.repoResults.value
                val resultCode = (repoResults?.second as? Results.Error)?.code

                if (repoResults == null || resultCode == Results.Error.CODE.PHONE_VERIFICATION_CODE_EXPIRED)
                    accountModel.verifyPhoneNumber(account.also {
                        it.cellphone = ParseUtil.formatPhone(it.cellphone!!)
                    }.cellphone!!, requireActivity())

                navController.navigate(R.id.action_phoneRegistrationFragment_to_verifyPhoneFragment,
                    Bundle().apply {
                        putString(Const.ACCOUNT, ParseUtil.toJson(account))
                    })
            } else navController.navigate(
                R.id.action_phoneRegistrationFragment_to_extendedRegistrationFragment,
                Bundle().apply {
                    putString(Const.ACCOUNT, ParseUtil.toJson(account))
                    putString(Const.AUTH_TYPE, AuthType.PHONE.name)
                })
        }
    }
}
