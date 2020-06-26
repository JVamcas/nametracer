package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch.OnChangeListener
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentRegistrationBinding
import com.petruskambala.namcovidcontacttracer.model.User
import com.petruskambala.namcovidcontacttracer.model.UserType
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_registration.*

/**
 * A simple [Fragment] subclass.
 */
open class RegistrationFragment : AbstractFragment() {
    lateinit var binding: FragmentRegistrationBinding
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = User()
        arguments?.let {
            val json = it.getString("user")
            user = ParseUtil.fromJson(json, User::class.java)
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
        binding.user = user

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.account_select_auto_layout,
            UserType.values().map { it.name })
        account_type.setAdapter(adapter)

        account_type.setOnItemClickListener { _, _, pos, _ ->
            user.userType = if(pos == 0) UserType.PERSONAL else UserType.BUSINESS
        }
        new_user_btn.setOnClickListener {
            showProgressBar("Creating your account")
            val password = password.text.toString()
            authModel.createNewUser(user, password)
            new_user_btn.isEnabled = false
            authModel.repoResults.observe(viewLifecycleOwner, Observer { mResults ->
                new_user_btn.isEnabled = true
                if (mResults is Results.Success) {
                    showToast("Account Created.")
                    navController.navigate(R.id.action_registrationFragment_to_loginFragment)
                } else super.parseRepoResults(mResults, "")
                authModel.clearRepoResults(viewLifecycleOwner)
                endProgressBar()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        activity.toolbar.navigationIcon = null
    }
}
