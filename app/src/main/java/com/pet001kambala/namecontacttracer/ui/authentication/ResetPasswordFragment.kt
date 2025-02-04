package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namecontacttracer.databinding.FragmentResetPasswordBinding
import com.pet001kambala.namecontacttracer.ui.ObserveOnce
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
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
                endProgressBar()
                reset_password_btn.isEnabled = false
                showProgressBar("Sending password reset email...")
                accountModel.resetPassword(email = email)
                accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
                    it.apply {
                        endProgressBar()
                        reset_password_btn.isEnabled = true
                        if (it.second is Results.Success) {
                            showToast("A password reset link has been sent to your email.")
                            navController.popBackStack()
                        }
                        else super.parseRepoResults(it.second, "")
                    }
                })
            }
        }
    }
}
