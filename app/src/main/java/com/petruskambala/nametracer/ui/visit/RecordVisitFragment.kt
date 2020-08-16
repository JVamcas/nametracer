package com.petruskambala.nametracer.ui.visit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.petruskambala.nametracer.databinding.FragmentRecordVisitBinding
import com.petruskambala.nametracer.model.Person
import com.petruskambala.nametracer.model.RecordVisit
import com.petruskambala.nametracer.model.Visit
import com.petruskambala.nametracer.ui.AbstractFragment
import com.petruskambala.nametracer.ui.ObserveOnce
import com.petruskambala.nametracer.utils.BindingUtil
import com.petruskambala.nametracer.utils.ParseUtil
import com.petruskambala.nametracer.utils.ParseUtil.Companion.isValidTemperature
import com.petruskambala.nametracer.utils.Results
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
            showProgressBar("Loading visitor's account...")

            accountModel.findAccount(email = email, phoneNumber = cell)

            accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
                it.apply {
                    endProgressBar()
                    if (second is Results.Success) {

                        val visit = Visit(
                            person = (first as Person).apply { placeVisited++ },
                            place = accountModel.currentAccount.value
                        ).apply { temperature = binding.visit?.visitorTemperature }
                        showProgressBar("Recording visit...")
                        visitModel.recordVisit(visit)

                    } else {
                        if (first == null)
                            showToast("Person must create account.")
                        else super.parseRepoResults(second, "")
                    }
                }
            })

        }
        visitModel.repoResults.observe(viewLifecycleOwner, ObserveOnce { res ->
            res.apply {
                endProgressBar()
                if (second is Results.Success) {
                    showToast("Visit recorded.")
                    navController.popBackStack()
                } else
                    super.parseRepoResults(second, "")
            }
        })
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
                    if (value.isNullOrEmpty() || ParseUtil.isValidMobile(value) || ParseUtil.isValidEmail(
                            value
                        )
                    )
                        null
                    else "Enter a valid email or cellphone number."
            }
        })
    }
}
