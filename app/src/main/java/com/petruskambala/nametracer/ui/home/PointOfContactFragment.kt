package com.petruskambala.nametracer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petruskambala.nametracer.R
import com.petruskambala.nametracer.databinding.FragmentPointOfContactBinding
import com.petruskambala.nametracer.model.Account
import com.petruskambala.nametracer.model.AccountType
import com.petruskambala.nametracer.model.Auth
import com.petruskambala.nametracer.ui.AbstractFragment
import com.petruskambala.nametracer.ui.ObserveOnce
import com.petruskambala.nametracer.utils.Const
import com.petruskambala.nametracer.utils.ParseUtil
import com.petruskambala.nametracer.utils.Results
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
