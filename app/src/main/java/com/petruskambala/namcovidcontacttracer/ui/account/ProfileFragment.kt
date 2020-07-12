package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
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
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        authModel.currentAccount.observe(viewLifecycleOwner, Observer {
            binding.person = Person(account = account)
        })
        return binding.root
    }
}
