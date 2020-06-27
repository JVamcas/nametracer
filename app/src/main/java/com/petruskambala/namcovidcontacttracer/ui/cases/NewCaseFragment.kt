package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentNewCaseBinding
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_new_case.*

/**
 * A simple [Fragment] subclass.
 */
open class NewCaseFragment : AbstractFragment() {

    val caseModel: CaseViewModel by activityViewModels()
    private lateinit var case: CovidCase
    private lateinit var binding: FragmentNewCaseBinding

    val accountModel: AccountViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        case = CovidCase()
        arguments?.apply {
            val json = getString(Const.CASE)
            case = ParseUtil.fromJson(json, CovidCase::class.java)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewCaseBinding.inflate(inflater,container,false)
        binding.covidCase = case
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        save_btn.setOnClickListener {
            save_btn.isEnabled = false
            caseModel.registerNewCase(case)
            caseModel.repoResults.observe(viewLifecycleOwner, Observer { result ->
                save_btn.isEnabled = true
                if(result is Results.Success){
                    showToast("Case registered successfully.")
                }else super.parseRepoResults(result,"")
                caseModel.clearRepoResults(viewLifecycleOwner)
            })
        }
    }

    fun findPerson(){
        find_user.isEnabled = false
        //TODO check if is email or ID or cellphone
        accountModel.findPerson()
    }
}
