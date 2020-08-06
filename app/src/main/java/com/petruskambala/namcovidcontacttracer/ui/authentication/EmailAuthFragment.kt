package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentEmailAuthBinding
import com.petruskambala.namcovidcontacttracer.model.Auth
import com.petruskambala.namcovidcontacttracer.model.AuthType
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel.AuthState.*
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_email_auth.*

/**
 * A simple [Fragment] subclass.
 */
class EmailAuthFragment : AbstractAuthFragment() {
    private lateinit var binding: FragmentEmailAuthBinding
    private lateinit var authType: AuthType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            authType = AuthType.valueOf(getString(Const.AUTH_TYPE)!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmailAuthBinding.inflate(inflater, container, false)
        binding.authType = authType
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Auth()
        binding.auth = auth

        create_new_account.setOnClickListener {
            navController.navigate(R.id.action_emailAuthFragment_to_selectSignUpModeFragment)
        }

        login_btn.setOnClickListener {
            login_btn.isEnabled = false
            showProgressBar("Authenticating...")

            authenticateWithEmail()
        }
        reset_password.setOnClickListener {
            navController.navigate(R.id.action_emailAuthFragment_to_resetPasswordFragment)
        }
    }

    private fun authenticateWithEmail() {
        val auth = binding.auth!!

        accountModel.authenticateWithEmail(auth.idMailCell, auth.password)
        accountModel.repoResults.observe(viewLifecycleOwner, Observer {
            it?.let {
                login_btn.isEnabled = true
                if (it.second is Results.Success) {
                    if (accountModel.authState.value == EMAIL_NOT_VERIFIED)
                        navController.navigate(R.id.action_emailAuthFragment_to_verifyEmailFragment)
                    else {//logged in and emailed verified
                        requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        navController.popBackStack(R.id.selectLoginModeFragment,true)
                    }
                } else (it.second is Results.Error)
                super.parseRepoResults(it.second, "")
                endAuthFlow()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}