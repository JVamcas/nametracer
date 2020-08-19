package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pet001kambala.namecontacttracer.MainActivity
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.model.AuthType
import com.pet001kambala.namecontacttracer.ui.account.AccountViewModel
import com.pet001kambala.namecontacttracer.utils.Const
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_select_sign_up_mode.*
/**
 * Subclass of [SelectSignUpModeFragment].
 */
class SelectLoginModeFragment : SelectSignUpModeFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.withEmailBtn.text = getString(R.string.sign_in_with_email)
        binding.withPhoneBtn.text = getString(R.string.sign_in_with_phone)
        binding.haveAnAccount.text = getString(R.string.create_account)

        with_email_btn.setOnClickListener {
            navController.navigate(
                R.id.action_selectLoginModeFragment_to_emailAuthFragment,
                Bundle().apply {
                    putString(Const.AUTH_TYPE, AuthType.EMAIL.name)
                })
        }
        with_phone_btn.setOnClickListener {
            navController.navigate(
                R.id.action_selectLoginModeFragment_to_phoneAuthFragment,
                Bundle().apply {
                    putString(Const.AUTH_TYPE, AuthType.PHONE.name)
                })
        }
        have_an_account.setOnClickListener {
            navController.navigate(R.id.action_selectLoginModeFragment_to_selectSignUpModeFragment)
        }//No account?
        accountModel.authState.observe(viewLifecycleOwner, Observer {
            if(it == AccountViewModel.AuthState.AUTHENTICATED)
                navController.navigate(R.id.action_global_homeFragment)
        })
    }

    override fun onBackClick() {
        showExitDialog()
    }
    override fun onResume() {
        super.onResume()
         (activity as MainActivity).toolbar.navigationIcon = null
    }
}
