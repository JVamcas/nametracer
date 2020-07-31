package com.petruskambala.namcovidcontacttracer.ui.visit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.databinding.FragmentRecordVisitBinding
import com.petruskambala.namcovidcontacttracer.model.Person
import com.petruskambala.namcovidcontacttracer.model.RecordVisit
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.BindingUtil
import com.petruskambala.namcovidcontacttracer.utils.DateUtil
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidNationalID
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidTemperature
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_new_case.email_cell_id
import kotlinx.android.synthetic.main.fragment_new_case.record_btn
import kotlinx.android.synthetic.main.fragment_record_visit.*

/**
 * A simple [Fragment] subclass.
 */
class RecordVisitFragment : AbstractFragment() {

    private lateinit var binding: FragmentRecordVisitBinding
    private val visitModel: VisitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordVisitBinding.inflate(layoutInflater, container, false)
        binding.visit = RecordVisit()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        record_btn.setOnClickListener {
            val idEmailCell = email_cell_id.text.toString()
            val email = if (ParseUtil.isValidEmail(idEmailCell)) idEmailCell else null
            val cell = if (ParseUtil.isValidMobile(idEmailCell)) idEmailCell else null
            val nationalID = if (ParseUtil.isValidNationalID(idEmailCell)) idEmailCell else null
            showProgressBar("Loading visitor's account...")
            accountModel.findAccount(email = email, phoneNumber = cell, nationalId = nationalID)

            accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                println("")
                it?.apply {
                    endProgressBar()
                    if (second is Results.Success) {
                        accountModel.clearRepoResults(viewLifecycleOwner)
                        val visit = Visit(
                            person = (first as Person).apply { placeVisited++ },
                            place = accountModel.currentAccount.value
                        ).apply {
                            time = DateUtil.today()
                            temperature = binding.visit?.visitorTemperature
                        }
                        updateProgressBarMsg("Recording visit...")
                        visitModel.recordVisit(visit)
                    } else
                        super.parseRepoResults(second, "")
                    accountModel.clearRepoResults(viewLifecycleOwner)
                }
            })
            visitModel.repoResults.observe(viewLifecycleOwner, Observer { res ->
                res?.apply {
                    endProgressBar()
                    if (second is Results.Success)
                        navController.popBackStack()
                    super.parseRepoResults(second, "Visit")
                    visitModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }

        visitor_temperature.addTextChangedListener(object : BindingUtil.TextChangeLister() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                visitor_temperature.error =
                    if (!isValidTemperature(p0.toString())) "Invalid temperature." else null
            }
        })
        email_cell_id.addTextChangedListener(object : BindingUtil.TextChangeLister() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val value = p0.toString()
                email_cell_id.error =
                    if (ParseUtil.isValidMobile(value) || ParseUtil.isValidEmail(value) || isValidNationalID(
                            value
                        )
                    ) null
                    else "Enter a valid ID, email or cellphone number."
            }
        })
    }
}
