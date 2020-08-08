package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentUpdateProfileBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.Gender
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.fragment_update_profile.account_type
import kotlinx.android.synthetic.main.fragment_update_profile.gender

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : AbstractFragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer { account ->
            account?.let {
                val copy = ParseUtil.copyOf(
                    if (account is Person) account else Person(account = account),
                    Person::class.java
                )
                binding.account = copy
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gender.apply {
            setAdapter(ArrayAdapter(
                requireContext(), R.layout.account_select_auto_layout,
                Gender.values().map { it.name }
            ))
        }
        account_type.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout,
                    arrayListOf("PERSONAL", "POINT OF CONTACT")
                )
            )
        }

        birth_date.setOnClickListener { selectDate { birth_date.setText(it) } }

        update_account.setOnClickListener {
            update_account.isEnabled = false
            showProgressBar("Updating your profile...")

            accountModel.updateAccount(binding.account!!)
            accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    update_account.isEnabled = true
                    if (it.second is Results.Success)
                        navController.popBackStack()
                    super.parseRepoResults(it.second, "Profile")
                    endAuthFlow()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
