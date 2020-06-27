package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentCaseListBinding
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.AbstractListFragment
import kotlinx.android.synthetic.main.fragment_case_list.*

/**
 * A simple [Fragment] subclass.
 */
class CaseListFragment : AbstractListFragment<CovidCase,CaseListAdapter.ViewHolder>(){

    private lateinit var binding: FragmentCaseListBinding
    private val caseModel: CaseViewModel by activityViewModels()

    override fun initAdapter() {
        mAdapter = CaseListAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCaseListBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        new_case_btn.setOnClickListener {
            navController.navigate(R.id.action_casesFragment_to_newCaseFragment)
        }
        caseModel.caseList.observe(viewLifecycleOwner, Observer {
            it?.apply { binding.caseCount = it.size }
        })
    }

    override fun onEditModel(model: CovidCase, isValid: Boolean, pos: Int) {

    }

    override fun onDeleteModel(modelPos: Int) {

    }

    override fun onModelClick(model: CovidCase) {

    }

    override fun onModelIconClick(model: CovidCase) {

    }
}
