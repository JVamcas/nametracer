package com.petruskambala.namcovidcontacttracer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentPointOfContactBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.Auth
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_point_of_contact.*

/**
 * A simple [Fragment] subclass.
 */
class PointOfContactFragment : AbstractFragment() {

    private lateinit var binding: FragmentPointOfContactBinding
    private val accountModel: AccountViewModel by activityViewModels()
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
            val nationalID = if (ParseUtil.isValidNationalID(idEmailCell)) idEmailCell else null

            accountModel.findAccount(email,cell,nationalID,AccountType.BUSINESS)

            accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                binding.account = null
                it?.apply {
                    endProgressBar()
                    find_user.isEnabled = true
                    if (second is Results.Success) {
                        binding.account = first as Account
                        requireActivity().toolbar.title = "Point of Contact"
                    }
                    else parseRepoResults(second,"")
                    accountModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }

        view_visitors.setOnClickListener {
            navController.navigate(R.id.action_findPointOfContactFragment_to_placeVisitorsFragment)
        }
    }
}
