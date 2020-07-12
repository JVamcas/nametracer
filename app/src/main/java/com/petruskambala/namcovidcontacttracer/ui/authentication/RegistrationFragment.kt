package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentRegistrationBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_registration.*

/**
 * A simple [Fragment] subclass.
 */
open class RegistrationFragment : AbstractFragment() {
    private lateinit var binding: FragmentRegistrationBinding
    lateinit var account: Account
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        account = Account()
        arguments?.let {
            val json = it.getString(Const.ACCOUNT)
            account = ParseUtil.fromJson(json, Account::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        new_account_btn.text = if (account.accountType == AccountType.PERSONAL) "NEXT" else "CREATE"
        binding.account = account

        account_type.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout,
                    AccountType.values().map { it.name })
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
                navController.navigate(
                    R.id.action_registrationFragment_to_extendedRegistrationFragment,
                    bundle
                )
            } else
                createNewUser(account, password)
        }
    }

    fun createNewUser(account: Account, password: String) {
        showProgressBar("Creating your account... Please wait!")
        authModel.createNewUser(account, password)
        new_account_btn.isEnabled = false
        authModel.repoResults.observe(viewLifecycleOwner, Observer { pair ->
            pair?.let {
                new_account_btn.isEnabled = true
                if (pair.second is Results.Success) {
                    showToast("Account created successfully.")
                    navController.navigate(R.id.action_global_loginFragment)
                } else super.parseRepoResults(pair.second, "")
                authModel.clearRepoResults(viewLifecycleOwner)
                endProgressBar()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        activity.toolbar.navigationIcon = null
    }
}
