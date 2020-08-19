package com.pet001kambala.namecontacttracer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentPointOfContactBinding
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.model.AccountType
import com.pet001kambala.namecontacttracer.model.Auth
import com.pet001kambala.namecontacttracer.ui.AbstractFragment
import com.pet001kambala.namecontacttracer.ui.ObserveOnce
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_point_of_contact.*

/**
 * A simple [Fragment] subclass.
 */
class PointOfContactFragment : AbstractFragment() {

    private lateinit var binding: FragmentPointOfContactBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPointOfContactBinding.inflate(inflater,container
        ,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.auth = Auth()

        find_user.setOnClickListener {
            find_user.isEnabled = false
            showProgressBar("Searching...")

            val idEmailCell = binding.auth!!.idMailCell
            val email = if (ParseUtil.isValidEmail(idEmailCell)) idEmailCell else null
            val cell = if (ParseUtil.isValidMobile(idEmailCell)) idEmailCell else null

            accountModel.findAccount(email,cell,AccountType.BUSINESS)

            accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
                binding.account = null
                it.apply {
                    endProgressBar()
                    find_user.isEnabled = true
                    if (second is Results.Success) {
                        binding.account = first as Account
                        requireActivity().toolbar.title = "Point of Contact"
                    }
                    else {
                        if (first == null)
                            showToast("Person must create account.")
                        else super.parseRepoResults(it.second, "")
                    }
                }
            })
        }

        view_visitors.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Const.PERSON_ID,binding.account!!.id)
            navController.navigate(R.id.action_findPointOfContactFragment_to_placeVisitorsFragment,bundle)
        }
    }
}
