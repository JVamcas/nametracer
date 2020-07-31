package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentExtendedRegistrationBinding
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.Gender
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import kotlinx.android.synthetic.main.fragment_extended_registration.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ExtendedRegistrationFragment : RegistrationFragment() {

    private lateinit var binding: FragmentExtendedRegistrationBinding
    private lateinit var password: String
    private lateinit var person: Person
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            password = getString(Const.PASSWORD)!!
            val account = ParseUtil.fromJson(getString(Const.ACCOUNT), Account::class.java)
            person = Person(account = account)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExtendedRegistrationBinding.inflate(inflater, container, false)
        binding.person = person
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gender.apply {
            setAdapter(ArrayAdapter(
                requireContext(), R.layout.account_select_auto_layout,
                Gender.values().map { it.name }
            ))
        }

        birth_date.setOnClickListener { selectDate { birth_date.setText(it) } }

        new_account_btn.setOnClickListener {
            createNewUser(person, password)
        }
    }

    override fun onBackClick() {
        navController.popBackStack()
    }
}
