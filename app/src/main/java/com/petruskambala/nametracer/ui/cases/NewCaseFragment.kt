package com.petruskambala.nametracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.petruskambala.nametracer.R
import com.petruskambala.nametracer.databinding.FragmentNewCaseBinding
import com.petruskambala.nametracer.model.Auth
import com.petruskambala.nametracer.model.CaseState
import com.petruskambala.nametracer.model.Cases
import com.petruskambala.nametracer.model.Person
import com.petruskambala.nametracer.ui.AbstractFragment
import com.petruskambala.nametracer.ui.ObserveOnce
import com.petruskambala.nametracer.utils.*
import com.petruskambala.nametracer.utils.Const
import com.petruskambala.nametracer.utils.ParseUtil.Companion.isValidEmail
import com.petruskambala.nametracer.utils.ParseUtil.Companion.isValidMobile
import com.petruskambala.nametracer.utils.ParseUtil.Companion.isValidNationalID
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_new_case.*

/**
 * A simple [Fragment] subclass.
 */
open class NewCaseFragment : AbstractFragment() {

    val caseModel: CaseViewModel by activityViewModels()

    lateinit var aCase: Cases

    //    var person: Person? = null
    lateinit var binding: FragmentNewCaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aCase = Cases()
        arguments?.apply {
            val json = getString(Const.CASE)
            aCase = ParseUtil.fromJson(json, Cases::class.java).also { it.person = Person() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewCaseBinding.inflate(inflater, container, false)
        binding.covidCase = aCase //person is non null when we swiped view to update
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.auth = Auth()

        view_place_visited.setOnClickListener {
            navController.navigate(
                R.id.action_global_placeVisitedFragment,
                Bundle().apply { putString(Const.PERSON_ID, aCase.personId) })
        }
        //TODO need to use transformation when searching for places visited

        case_state.setOnClickListener {
            case_state.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout,
                    CaseState.values().map { it.name })
            )
        }

        record_btn.setOnClickListener {
            record_btn.isEnabled = false
//            case.timestamp = DateUtil.today()
            showProgressBar("Recording new case...")
            caseModel.registerNewCase(aCase)
            caseModel.repoResults.observe(viewLifecycleOwner, ObserveOnce { pair ->
                pair.let {
                    endProgressBar()
                    record_btn.isEnabled = true
                    if (pair.second is Results.Success) {
                        showToast("Case recorded successfully.")
                        navController.popBackStack()
                    } else super.parseRepoResults(pair.second, "")
                }
            })
        }
        email_cell_id.addTextChangedListener(object : BindingUtil.TextChangeLister() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val value = p0.toString()
                email_cell_id.error =
                    if (isValidMobile(value) || isValidEmail(value) || isValidNationalID(value)) null
                    else "Enter a valid email or cellphone number."
                find_user.isEnabled =
                    (isValidMobile(value) || isValidEmail(value) || isValidNationalID(value))
            }
        })
        find_user.setOnClickListener { findPerson() }
    }

    open fun findPerson() {

        val idEmailCell = binding.auth!!.idMailCell
        val email = if (isValidEmail(idEmailCell)) idEmailCell else null
        val cell = if (isValidMobile(idEmailCell)) idEmailCell else null

        caseModel.caseList.value?.apply {

            if (find {
                    (it.email == email && !email.isNullOrEmpty())
                            || (it.cellphone == cell && !cell.isNullOrEmpty())
                } != null)
                showToast("Case already recorded.")
            else {
                find_user.isEnabled = false
                showProgressBar("Loading info...")
                accountModel.findAccount(email = email, phoneNumber = cell)
                accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
                    it.apply {
                        endProgressBar()
                        find_user.isEnabled = true
                        if (second is Results.Success) {
                            aCase = Cases(_person = first as Person)
                            requireActivity().toolbar.title = "Record New Case"
                            binding.covidCase = aCase
                        } else {
                            if (first == null)
                                showToast("Person must create account.")
                            else super.parseRepoResults(it.second, "")
                        }
                    }
                })
            }
        }
    }
}
