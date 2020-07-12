package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentNewCaseBinding
import com.petruskambala.namcovidcontacttracer.model.Auth
import com.petruskambala.namcovidcontacttracer.model.CaseState
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.*
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidEmail
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidMobile
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidNationalID
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_new_case.*

/**
 * A simple [Fragment] subclass.
 */
open class NewCaseFragment : AbstractFragment() {

    val caseModel: CaseViewModel by activityViewModels()
    val accountModel: AccountViewModel by activityViewModels()

    private lateinit var case: CovidCase
    lateinit var binding: FragmentNewCaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        case = CovidCase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewCaseBinding.inflate(inflater, container, false)
        binding.covidCase = case
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.auth = Auth()

        case_state.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout,
                    CaseState.values().map { it.name })
            )
        }

        record_btn.setOnClickListener {
            record_btn.isEnabled = false
            case.time = DateUtil.today()
            showProgressBar("Registering new case...")
            caseModel.registerNewCase(case)
            caseModel.repoResults.observe(viewLifecycleOwner, Observer { pair ->
                pair?.let {
                    endProgressBar()
                    record_btn.isEnabled = true
                    if (pair.second is Results.Success) {
                        showToast("Case registered successfully.")
                        navController.popBackStack()
                    } else super.parseRepoResults(pair.second, "")
                    caseModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }
        email_cell_id.addTextChangedListener(object : BindingUtil.TextChangeLister() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val value = p0.toString()
                email_cell_id.error =
                    if (isValidMobile(value) || isValidEmail(value)) null
                    else "Enter a valid ID, email or cellphone number."
                find_user.isEnabled = (isValidMobile(value) || isValidEmail(value))
            }
        })
        find_user.setOnClickListener { findPerson() }
    }

    private fun findPerson() {
        find_user.isEnabled = false
        val idEmailCell = binding.auth!!.idMailCell
        val email = if (isValidEmail(idEmailCell)) idEmailCell else null
        val cell = if (isValidMobile(idEmailCell)) idEmailCell else null
        val nationalID = if (isValidNationalID(idEmailCell)) idEmailCell else null

        showProgressBar("Loading info...")
        accountModel.findPerson(email = email, phoneNumber = cell, nationalId = nationalID)
        accountModel.repoResults.observe(viewLifecycleOwner, Observer {
            it?.let {
                endProgressBar()
                find_user.isEnabled = true
                if (it.second is Results.Success) {
                    case = CovidCase(person = it.first)
                    requireActivity().toolbar.title = "Record New Case"
                    binding.person = it.first
                    binding.covidCase = case
                } else
                    super.parseRepoResults(it.second, "")
                accountModel.clearRepoResults(viewLifecycleOwner)
            }
        })
    }
}
