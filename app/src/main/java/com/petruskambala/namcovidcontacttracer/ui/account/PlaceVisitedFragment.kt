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

    private lateinit var binding: FragmentPlaceVisitedBinding
    private val visitModel: VisitViewModel by activityViewModels()
    private lateinit var personId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            personId = getString(Const.PERSON_ID)!!
            visitModel.switchPerson(personId)
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
            it?.let {
                initTable(it)
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

    private fun initTable(visits: ArrayList<Visit>?) {
        visits?.apply {
            val columnHeader = arrayListOf(
                ColumnHeader("Name"),
                ColumnHeader("Address"),
                ColumnHeader("Temperature"),
                ColumnHeader("Date")
            )
            val rowHeader = visits.map { t ->
                RowHeader((indexOfFirst { it.id == t.id } + 1).toString())
            } as ArrayList<RowHeader>

            val cells = visits.map {
                arrayListOf(
                    Cell(it.place?.name ?: "No Name"),
                    Cell(it.place.toString()),
                    Cell(if (it.temperature != null) it.temperature + "\u00B0C" else "Unknown"),
                    Cell(it.time)
                )
            }
            val tableAdapter =
                PlaceVisitedTableAdapter()
            place_visited_table.setAdapter(tableAdapter)
            tableAdapter.setAllItems(columnHeader, rowHeader, cells)

        }
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
                        if (visitModel.personVisits.value == null)
                            showProgressBar("Loading your travel history...")
                        visitModel.personVisits.observe(viewLifecycleOwner, Observer { visits ->
                            visits?.apply {
                                val file = File(path, "${first().person?.name} Travel History.xlsx")

                                exportPersonTravelHistory(Workbook.createWorkbook(file), this)
                                showToast("Saved to: ${file.absolutePath}")
                            }
                        })
                        visitModel.repoResults.observe(viewLifecycleOwner, Observer {
                            it?.apply {
                                endProgressBar()
                                if (second is Results.Error)
                                    parseRepoResults(second, "")
                                visitModel.clearRepoResults(viewLifecycleOwner)
                            }
                        })
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
