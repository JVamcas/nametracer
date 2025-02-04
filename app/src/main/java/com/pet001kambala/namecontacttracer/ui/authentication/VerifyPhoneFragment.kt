package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.firebase.auth.PhoneAuthProvider
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentVerifyPhoneBinding
import com.pet001kambala.namecontacttracer.model.AccountType
import com.pet001kambala.namecontacttracer.model.Person
import com.pet001kambala.namecontacttracer.model.PhoneAuthCred
import com.pet001kambala.namecontacttracer.utils.BindingUtil
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
import com.pet001kambala.namecontacttracer.utils.Results.Success.CODE.*
import com.pet001kambala.namecontacttracer.utils.Results.Error.CODE.*

import kotlinx.android.synthetic.main.fragment_verify_phone.*

/**
 * A simple [Fragment] subclass.
 */
class VerifyPhoneFragment : AbstractAuthFragment() {


    private lateinit var binding: FragmentVerifyPhoneBinding
    private lateinit var phone: String
    private var account: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            val json = getString(Const.ACCOUNT)
            account = ParseUtil.fromJson(json, Person::class.java)
            json?.apply {
                if (account!!.accountType == AccountType.PERSONAL)
                    account = ParseUtil.fromJson(this, Person::class.java)
            }
            phone = getString(Const.PHONE_NUMBER) ?: account!!.cellphone!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerifyPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resend_verification_code.setOnClickListener {
            accountModel.verifyPhoneNumber(phone, requireActivity())
        }


        submit_btn.setOnClickListener {
            submit_btn.isEnabled = false
            showProgressBar("Authenticating...")

            val code = verification_code.text.toString()
            val results = accountModel.repoResults.value?.peekContent()

            results?.apply {
                if ((second as? Results.Success)?.code == PHONE_VERIFY_CODE_SENT) {
                    val verificationId = (first as? PhoneAuthCred)?.verificationId
                    accountModel.signInWithPhoneAuthCredential(
                        account = account,
                        phoneCredential = PhoneAuthProvider.getCredential(
                            verificationId!!,
                            code
                        )
                    )
                }
            }
        }

        accountModel.repoResults.observe(viewLifecycleOwner, Observer {
            it?.peekContent()?.apply {
                endProgressBar()
                (second as? Results.Error)?.code.apply {
                    if (this == PHONE_VERIFICATION_CODE_EXPIRED)
                        showToast("Verification code expired.")
                    else parseRepoResults(second, "")
                }
                (second as? Results.Success)?.code.apply {
                    when {
                        this == PHONE_VERIFY_CODE_SENT -> showToast("Verification code sent.")

                        this == PHONE_VERIFY_SUCCESS -> {
                            val phoneCred = (first as PhoneAuthCred).phoneAuthCredential
                            accountModel.signInWithPhoneAuthCredential(
                                account = account,
                                phoneCredential = phoneCred!!
                            )
                        }
                        this == AUTH_SUCCESS -> {
                            accountModel.clearRepoResults(viewLifecycleOwner)
                            navController.popBackStack(R.id.selectLoginModeFragment, false)
                        }
                    }
                }
            }
        })

        verification_code.addTextChangedListener(
            object : BindingUtil.TextChangeLister() {
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    super.onTextChanged(p0, p1, p2, p3)
                    val code = p0.toString()
                    val isValidPhone = (code.isEmpty() || ParseUtil.isValidAuthCode(code))
                    verification_code.error =
                        if (isValidPhone) null else "Invalid verification code."
                    submit_btn.isEnabled = ParseUtil.isValidAuthCode(code)
                }
            })
    }
}
