package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.firebase.firestore.FieldValue
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.model.Account
import com.petruskambala.namcovidcontacttracer.model.CaseState
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.utils.DateUtil
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_new_case.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateCaseFragment : NewCaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //used when swiped on case to update
        case.person?.let { requireActivity().toolbar.title = "Update Case Status"}
        record_btn.text = getString(R.string.update_btn_txt)

        find_user.setOnClickListener { findPerson()}

        record_btn.setOnClickListener {
            record_btn.isEnabled = false
            showProgressBar("Updating case status...")
            caseModel.updateCase(binding.covidCase!!.also { it.time = DateUtil.today() })
            caseModel.repoResults.observe(viewLifecycleOwner, Observer {
                it?.let {
                    record_btn.isEnabled = true
                    endProgressBar()
                    if (it.second is Results.Success)
                        navController.popBackStack()
                    super.parseRepoResults(it.second, "Case")
                    caseModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }
    }

    override fun findPerson(){
        find_user.isEnabled = false
        val idEmailCell = binding.auth!!.idMailCell
        val email = if (ParseUtil.isValidEmail(idEmailCell)) idEmailCell else null
        val cell = if (ParseUtil.isValidMobile(idEmailCell)) idEmailCell else null

        showProgressBar("Loading info...")
        caseModel.findCase(email = email, cellphone = cell)
        caseModel.clearRepoResults(viewLifecycleOwner)

        caseModel.repoResults.observe(viewLifecycleOwner, Observer {
            println("called with $it")
            it?.apply {
                endProgressBar()
                find_user.isEnabled = true
                if (second is Results.Success) {
                    binding.covidCase = first
                    requireActivity().toolbar.title = "Update Case Status"
                } else
                    super.parseRepoResults(it.second, "")
                caseModel.clearRepoResults(viewLifecycleOwner)
            }
        })
    }
}
