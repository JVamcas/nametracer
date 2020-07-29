package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentPlaceVisitedBinding
import com.petruskambala.namcovidcontacttracer.model.Cell
import com.petruskambala.namcovidcontacttracer.model.ColumnHeader
import com.petruskambala.namcovidcontacttracer.model.RowHeader
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.ui.visit.VisitViewModel
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.Results
import jxl.Workbook
import kotlinx.android.synthetic.main.fragment_place_visited.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
open class PlaceVisitedFragment : AbstractFragment() {

    lateinit var binding: FragmentPlaceVisitedBinding
    val visitModel: VisitViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(Const.PERSON_ID)?.let { visitModel.switchPerson(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentPlaceVisitedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placesCount = 0
        visitModel.personVisits.observe(viewLifecycleOwner, Observer {
            binding.placesCount = it?.size ?: 0
            it?.apply {
                val visit = first()
                val colHeader = visit.placeColumns.map { ColumnHeader(it) } as ArrayList
                val rows = map { it.placeData.map { Cell(it ?: "Unknown") } as ArrayList}
                val rowHeader = map { t ->
                    RowHeader((indexOfFirst { it.id == t.id } + 1).toString())
                } as ArrayList<RowHeader>
                initTable(colHeader,rows,rowHeader)
            }
        })

        visitModel.modelLoadState.observe(viewLifecycleOwner, Observer {
            when (it.first) {
                AbstractViewModel.LoadState.LOADING -> showProgressBar("Loading places visited...")
                AbstractViewModel.LoadState.LOADED -> {
                    endProgressBar()
                    if (it.second is Results.Error)
                        parseRepoResults(it.second, "")
                }
                AbstractViewModel.LoadState.NO_LOAD -> {
                }
            }
        })
    }

    fun initTable(colHeader: ArrayList<ColumnHeader>,rows: List<ArrayList<Cell>>,rowHeader: ArrayList<RowHeader>) {
            val tableAdapter =
                PlaceVisitedTableAdapter()
            place_visited_table.setAdapter(tableAdapter)
            tableAdapter.setAllItems(colHeader, rowHeader, rows)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.travel_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.export_to_excel) {
            if (isStoragePermissionGranted()) {//permission must have been granted
                getStorageDir { path ->
                    path?.let {
                        visitModel.personVisits.value?.apply {
                            val file = File(path, "${first().person?.name} Travel History.xlsx")
                            exportPersonTravelHistory(Workbook.createWorkbook(file), this)
                            showToast("Saved to: ${file.absolutePath}")
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
