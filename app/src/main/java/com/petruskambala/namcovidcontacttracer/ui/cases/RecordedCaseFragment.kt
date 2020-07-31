package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentRecordedCaseBinding
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.AbstractListFragment
import com.petruskambala.namcovidcontacttracer.ui.visit.VisitViewModel
import com.petruskambala.namcovidcontacttracer.utils.AccessType
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil
import com.petruskambala.namcovidcontacttracer.utils.Results
import jxl.Workbook
import jxl.write.WritableWorkbook
import kotlinx.android.synthetic.main.fragment_recorded_case.*
import java.io.File
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 */
class RecordedCaseFragment : AbstractListFragment<CovidCase, CaseListAdapter.ViewHolder>() {

    private lateinit var binding: FragmentRecordedCaseBinding
    private val caseModel: CaseViewModel by activityViewModels()
    private val visitModel: VisitViewModel by activityViewModels()

    override fun initAdapter() {
        mAdapter = CaseListAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentRecordedCaseBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleRecycleView(caseList, this)
        caseModel.caseList.observe(viewLifecycleOwner, Observer {
            it?.apply {
                binding.caseCount = it.size
                mAdapter.modelList = it
            }
        })

        new_case_btn.setOnClickListener {
            navController.navigate(R.id.action_casesFragment_to_newCaseFragment)
        }
    }

    override fun onEditModel(model: CovidCase, pos: Int) {
        if (validateOp(AccessType.UPDATE_CASE)) {
            val bundle = Bundle().apply {
                putString(Const.CASE, ParseUtil.toJson(model))
                putInt(Const.MODEL_POS, pos)
            }
            navController.navigate(R.id.action_casesFragment_to_updateCaseFragment, bundle)

        } else showToast("Err: Permission denied.")
    }

    override fun onDeleteModel(modelPos: Int) {

    }

    override fun onModelClick(model: CovidCase) {

    }

    override fun onModelIconClick(model: CovidCase) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.recorded_case_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.find_case -> {
                navController.navigate(R.id.action_casesFragment_to_updateCaseFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
