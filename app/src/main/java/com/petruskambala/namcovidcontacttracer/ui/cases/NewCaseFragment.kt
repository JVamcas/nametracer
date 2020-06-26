package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_new_case.*

/**
 * A simple [Fragment] subclass.
 */
open class NewCaseFragment : AbstractFragment() {

    val caseModel: CaseViewModel by activityViewModels()
    private lateinit var case: CovidCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        case = CovidCase()
        arguments?.apply {
            val json = getString("case")
            case = ParseUtil.fromJson(json, CovidCase::class.java)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_case, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        save_btn.setOnClickListener {
            save_btn.isEnabled = false
            caseModel.registerNewCase(case)
            caseModel.repoResults.observe(viewLifecycleOwner, Observer { result ->
                if(result is Results.Success){
                    showToast("Case registered successfully.")
                }else super.parseRepoResults(result,"")
                caseModel.clearRepoResults(viewLifecycleOwner)
            })
        }
    }

    fun findPerson(){

    }
}
