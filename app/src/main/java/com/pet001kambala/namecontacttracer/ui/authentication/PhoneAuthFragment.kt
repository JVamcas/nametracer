package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
import com.pet001kambala.namecontacttracer.utils.Results.Error.CODE.*
import kotlinx.android.synthetic.main.fragment_phone_auth.*

/**
 * A simple [Fragment] subclass.
 */
class PhoneAuthFragment : AbstractAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_btn.setOnClickListener {
            login_btn.isEnabled = false
            val phone = phone_number.text.toString()

            val repoResults = accountModel.repoResults.value?.peekContent()
            val resultCode = (repoResults?.second as? Results.Error)?.code

            if (repoResults == null || resultCode == PHONE_VERIFICATION_CODE_EXPIRED)
                accountModel.verifyPhoneNumber(phone, requireActivity())

            val account = Account().also { it.cellphone = phone }
            navController.navigate(R.id.action_phoneAuthFragment_to_verifyPhoneFragment,
                Bundle().apply { putString(Const.ACCOUNT, ParseUtil.toJson(account)) })
        }
        phone_number.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val phone = phone_number.text.toString()
                val isValidPhone = (phone.isEmpty() || ParseUtil.isValidMobile(phone))
                phone_number.error = if (isValidPhone) null else "Invalid phone number."
                login_btn.isEnabled = ParseUtil.isValidMobile(phone)
            }
        })
    }
}
