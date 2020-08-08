package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentEmailRegistrationBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.AuthType
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel.AuthState.EMAIL_NOT_VERIFIED
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_email_registration.*

/**
 * A simple [Fragment] subclass.
 */
open class EmailRegistrationFragment : AbstractAuthFragment() {
    private lateinit var binding: FragmentEmailRegistrationBinding
    lateinit var account: Account
    open var authType: AuthType = AuthType.EMAIL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        account = Account()
        arguments?.apply {
            val json = getString(Const.ACCOUNT)
            account = if (json != null) ParseUtil.fromJson(json, Account::class.java) else Account()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmailRegistrationBinding.inflate(inflater, container, false)
        binding.authType = authType
        binding.account = account
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        new_account_btn.text = if (account.accountType == AccountType.PERSONAL) "NEXT" else "CREATE"

        account_type.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout, arrayListOf("PERSONAL", "POINT OF CONTACT")
                )
            )
            setOnItemClickListener { _, _, pos, _ ->
                new_account_btn.text = if (pos == 0) "NEXT" else "CREATE"
            }
        }

        new_account_btn.setOnClickListener {
            val password = password.text.toString()
            if (account.accountType == AccountType.PERSONAL) {
                val bundle = Bundle()
                bundle.putString(Const.ACCOUNT, ParseUtil.toJson(account))
                bundle.putString(Const.PASSWORD, password)
                bundle.putString(Const.AUTH_TYPE,AuthType.EMAIL.name)
                navController.navigate(
                    R.id.action_registrationFragment_to_extendedRegistrationFragment,
                    bundle
                )
            } else
                createNewUser(account.also { ParseUtil.formatPhone(it.cellphone?:"") }, password)
        }
    }

    fun createNewUser(account: Account, password: String) {
        showProgressBar("Creating your account... Please wait!")
        accountModel.createNewUser(account, password)
        new_account_btn.isEnabled = false
        accountModel.repoResults.observe(viewLifecycleOwner, Observer { pair ->
            pair?.let {
                endProgressBar()
                new_account_btn.isEnabled = true
                if (pair.second is Results.Success) {
                    showToast("Account created successfully.")
                    if (accountModel.authState.value == EMAIL_NOT_VERIFIED)
                        navController.navigate(R.id.action_global_verifyEmailFragment)
                    else {
                        requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        navController.popBackStack(R.id.selectLoginModeFragment,true)
                    }
                } else super.parseRepoResults(pair.second, "")
                accountModel.clearRepoResults(viewLifecycleOwner)
            }
        })
    }
}
