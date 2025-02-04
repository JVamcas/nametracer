package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import com.pet001kambala.namecontacttracer.MainActivity
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentEmailAuthBinding
import com.pet001kambala.namecontacttracer.model.Auth
import com.pet001kambala.namecontacttracer.model.AuthType
import com.pet001kambala.namecontacttracer.ui.ObserveOnce
import com.pet001kambala.namecontacttracer.ui.account.AccountViewModel.AuthState.*
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.Results
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
        accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
            it.let {
                endProgressBar()
                login_btn.isEnabled = true
                val results = it.second
                if (results is Results.Success) {
                    val authState = accountModel.authState.value
                    if (authState == EMAIL_NOT_VERIFIED)
                        navController.navigate(R.id.action_emailAuthFragment_to_verifyEmailFragment)
                    else {//logged in and emailed verified
                        requireActivity().drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        navController.popBackStack(R.id.selectLoginModeFragment, false)
                    }
                } else
                    super.parseRepoResults(results, "")
//                endAuthFlow()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
}