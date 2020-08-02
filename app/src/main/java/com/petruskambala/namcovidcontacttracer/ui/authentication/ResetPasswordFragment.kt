package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentResetPasswordBinding
import com.petruskambala.namcovidcontacttracer.model.Auth
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.reset_password
import kotlinx.android.synthetic.main.fragment_new_case.*
import kotlinx.android.synthetic.main.fragment_reset_password.*

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : AbstractAuthFragment() {

    private lateinit var binding: FragmentResetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reset_password_btn.setOnClickListener {
            val email = email_reset.text.toString()
            if (!ParseUtil.isValidEmail(email))
                showToast("Invalid email address.")
            else {
                reset_password_btn.isEnabled = false
                showProgressBar("Sending password reset email...")
                accountModel.resetPassword(email = email)
                accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                    it?.apply {
                        reset_password_btn.isEnabled = true
                        if (it.second is Results.Success) {
                            showToast("A password reset link has been sent to your email")
                            navController.popBackStack()
                        }
                        else super.parseRepoResults(it.second, "")
                        endAuthFlow()
                    }
                })
            }
        }
    }
}
