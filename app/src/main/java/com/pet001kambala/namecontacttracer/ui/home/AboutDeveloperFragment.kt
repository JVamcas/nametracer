package com.pet001kambala.namecontacttracer.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namecontacttracer.databinding.FragmentAboutDeveloperBinding
import com.pet001kambala.namecontacttracer.model.Account
import com.pet001kambala.namecontacttracer.model.Person
import com.pet001kambala.namecontacttracer.ui.AbstractFragment

/**
 * A simple [Fragment] subclass.
 */
class AboutDeveloperFragment : AbstractFragment() {


    private lateinit var binding: FragmentAboutDeveloperBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAboutDeveloperBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.person =
            Person(
                Account(
                    _name = "Petrus Mesias Kambala",
                    _cellphone = "+264 81 326 4666",
                    _email = "kmbpet001@myuct.ac.za",
                    _town = "Namibia",
                    _address_1 = "Windhoek"
                )
            )
    }
}
