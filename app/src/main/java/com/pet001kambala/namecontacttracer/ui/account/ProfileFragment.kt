package com.pet001kambala.namecontacttracer.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentProfileBinding
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.ui.AbstractFragment
import com.pet001kambala.namecontacttracer.utils.ParseUtil

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
            R.id.update_account -> {navController.navigate(R.id.action_global_updateProfileFragment)}
        }
        return super.onOptionsItemSelected(item)
    }
}
