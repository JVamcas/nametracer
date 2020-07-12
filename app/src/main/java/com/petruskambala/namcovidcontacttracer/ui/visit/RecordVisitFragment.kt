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
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.BindingUtil
import com.petruskambala.namcovidcontacttracer.utils.DateUtil
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_new_case.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RecordVisitFragment : AbstractFragment() {

    private lateinit var binding: FragmentRecordVisitBinding
    private val visitModel: VisitViewModel by activityViewModels()
    private val accountModel: AccountViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordVisitBinding.inflate(layoutInflater, container, false)
        binding.visit = Visit()
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

            accountModel.findPerson(email = email, phoneNumber = cell, nationalId = nationalID)
            accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    endProgressBar()
                    if (second is Results.Success) {

                        accountModel.clearRepoResults(viewLifecycleOwner)
                        val visit = Visit(
                            person = first as Person,
                            place = authModel.currentAccount.value
                        ).apply {
                            time = DateUtil.today()
                            temperature = binding.visit?.temperature
                        }

                        updateProgressBarMsg("Recording visit...")
                        visitModel.recordVisit(visit)
                        visitModel.repoResults.observe(viewLifecycleOwner, Observer { res ->
                            res?.apply {
                                endProgressBar()
                                if (second is Results.Success)
                                    navController.popBackStack()
                                super.parseRepoResults(second, "Visit")
                                visitModel.clearRepoResults(viewLifecycleOwner)
                            }
                        })
                    } else super.parseRepoResults(second, "")
                    accountModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }

        email_cell_id.addTextChangedListener(object : BindingUtil.TextChangeLister() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                super.onTextChanged(p0, p1, p2, p3)
                val value = p0.toString()
                email_cell_id.error =
                    if (ParseUtil.isValidMobile(value) || ParseUtil.isValidEmail(value)) null
                    else "Enter a valid ID, email or cellphone number."
                record_btn.isEnabled = (ParseUtil.isValidMobile(value) || ParseUtil.isValidEmail(
                    value
                ))
            }
        })
    }
}
