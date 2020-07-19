package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentLoginBinding
import com.petruskambala.namcovidcontacttracer.model.Auth
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : AbstractFragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Auth()
        binding.auth = auth

        create_new_account.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        login_btn.setOnClickListener {
            login_btn.isEnabled = false
            authModel.authenticate(auth.idMailCell, auth.password)
            showProgressBar("Authenticating...")
            authModel.repoResults.observe(viewLifecycleOwner, Observer {
                it?.let {
                    endProgressBar()
                    login_btn.isEnabled = true
                    if (it.second is Results.Success) {
                        activity?.drawer_layout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        navController.popBackStack(R.id.homeFragment, false)
                        showToast("Welcome back!")
                    } else super.parseRepoResults(it.second, "")
                    authModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }
    }

    override fun onBackClick() {
        showExitDialog()
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        activity.toolbar.navigationIcon = null
    }
}