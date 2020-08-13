package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.ui.ObserveOnce
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
        record_btn.isEnabled = true

        find_user.setOnClickListener { findPerson()}

        record_btn.setOnClickListener {
            record_btn.isEnabled = false
            showProgressBar("Updating case status...")
            caseModel.updateCase(binding.covidCase!!.also { it.time = DateUtil.today() })
        }

        caseModel.repoResults.observe(viewLifecycleOwner,ObserveOnce{
            it.apply {
                record_btn.isEnabled = true
                find_user.isEnabled = true
                endProgressBar()
                (second as? Results.Success)?.code.apply {
                    when {
                        this == Results.Success.CODE.LOAD_SUCCESS -> {
                            binding.covidCase = first
                            requireActivity().toolbar.title = "Update Case Status"
                        }
                        this == Results.Success.CODE.UPDATE_SUCCESS -> {
                            navController.popBackStack()
                        }
                        else -> super.parseRepoResults(second,"")
                    }
                }
                (second as? Results.Error)?.let { super.parseRepoResults(second,"") }
            }
        })
    }

    override fun findPerson(){
        find_user.isEnabled = false
        val idEmailCell = binding.auth!!.idMailCell
        val email = if (ParseUtil.isValidEmail(idEmailCell)) idEmailCell else null
        val cell = if (ParseUtil.isValidMobile(idEmailCell)) idEmailCell else null

        showProgressBar("Loading info...")
        caseModel.findCase(email = email, cellphone = cell)
    }
}
