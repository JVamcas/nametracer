package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentProfileBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : AbstractFragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            val json = getString("account")
            account = ParseUtil.fromJson(json,Account::class.java)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {
            it.apply {
                binding.person = it
            }
        })
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.update_account -> {navController.navigate(R.id.action_profileFragment_to_updateProfileFragment)}
        }
        return super.onOptionsItemSelected(item)
    }
}
