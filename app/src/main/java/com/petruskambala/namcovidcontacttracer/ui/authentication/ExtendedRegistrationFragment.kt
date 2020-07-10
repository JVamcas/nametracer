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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            password = getString(Const.PASSWORD)!!
            account = ParseUtil.fromJson(getString(Const.ACCOUNT),Account::class.java)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExtendedRegistrationBinding.inflate(inflater,container,false)
        binding.account = account
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gender.apply {
            setAdapter(ArrayAdapter(
                requireContext(), R.layout.account_select_auto_layout,
                Gender.values().map { it.name }
            ))
        }

        birth_date.setOnClickListener {
            DatePickerFragment(birth_date).show(childFragmentManager,"Date of Birth")
        }

        new_account_btn.setOnClickListener {
            createNewUser(account,password)
        }
    }

    class DatePickerFragment(
        private val mView: TextInputEditText
    ): DialogFragment(),DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireActivity(),this,year,month,day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            mView.setText("$year - ${view.month} - $day")
        }
    }
}
