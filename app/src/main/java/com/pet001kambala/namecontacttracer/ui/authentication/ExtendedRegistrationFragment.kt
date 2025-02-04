package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentExtendedRegistrationBinding
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.model.AuthType
import com.pet001kambala.namecontacttracer.model.Gender
import com.pet001kambala.namecontacttracer.model.Person
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_extended_registration.*

/**
 * A simple [Fragment] subclass.
 */
class ExtendedRegistrationFragment : EmailRegistrationFragment() {

    private lateinit var binding: FragmentExtendedRegistrationBinding
    private lateinit var password: String
    private lateinit var person: Person
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            password = getString(Const.PASSWORD) ?: ""
            val account = ParseUtil.fromJson(getString(Const.ACCOUNT), Account::class.java)
            person = Person(account = account)
            authType = AuthType.valueOf(getString(Const.AUTH_TYPE)!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExtendedRegistrationBinding.inflate(inflater, container, false)
        binding.person = person
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gender.apply {
            setAdapter(ArrayAdapter(
                requireContext(), R.layout.account_select_auto_layout,
                Gender.values().map { it.name }
            ))
        }

        birth_date.setOnClickListener { selectDate { birth_date.setText(it) } }

        new_account_btn.setOnClickListener {
            if (authType == AuthType.EMAIL)
                createNewUser(person, password)
            else {
                val repoResults = accountModel.repoResults.value?.peekContent()
                val resultCode = (repoResults?.second as? Results.Error)?.code

                if (repoResults == null || resultCode == Results.Error.CODE.PHONE_VERIFICATION_CODE_EXPIRED)
                    accountModel.verifyPhoneNumber(account.cellphone!!, requireActivity())

                navController.navigate(
                    R.id.action_extendedRegistrationFragment_to_verifyPhoneFragment,
                    Bundle().apply {
                        putString(Const.ACCOUNT, ParseUtil.toJson(person))
                    })
            }
        }
    }
}
