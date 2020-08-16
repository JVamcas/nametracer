package com.petruskambala.nametracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petruskambala.nametracer.R
import com.petruskambala.nametracer.databinding.FragmentSelectSignUpModeBinding
import com.petruskambala.nametracer.model.AuthType
import com.petruskambala.nametracer.utils.Const
import kotlinx.android.synthetic.main.fragment_select_sign_up_mode.*

/**
 * A simple [Fragment] subclass.
 */
open class SelectSignUpModeFragment : AbstractAuthFragment() {

    lateinit var binding: FragmentSelectSignUpModeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectSignUpModeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with_email_btn.setOnClickListener {
            navController.navigate(
                R.id.action_selectSignUpModeFragment_to_emailRegistrationFragment,
                Bundle().apply {
                    putString(Const.AUTH_TYPE, AuthType.EMAIL.name)
                })
        }
        with_phone_btn.setOnClickListener {
            navController.navigate(
                R.id.action_selectSignUpModeFragment_to_phoneRegistrationFragment,
                Bundle().apply {
                    putString(Const.AUTH_TYPE, AuthType.PHONE.name)
                })
        }
        have_an_account.setOnClickListener { navController.popBackStack() }
    }

    override fun onBackClick() {
        navController.popBackStack(R.id.selectLoginModeFragment,false)
    }
}
