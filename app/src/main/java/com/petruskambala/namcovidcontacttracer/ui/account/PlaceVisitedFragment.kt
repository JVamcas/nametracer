package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.PlaceVisitedTableAdapter
import com.petruskambala.namcovidcontacttracer.databinding.FragmentPlaceVisitedBinding
import com.petruskambala.namcovidcontacttracer.model.Cell
import com.petruskambala.namcovidcontacttracer.model.ColumnHeader
import com.petruskambala.namcovidcontacttracer.model.RowHeader
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.visit.VisitViewModel
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.Results
import kotlinx.android.synthetic.main.fragment_place_visited.*


/**
 * A simple [Fragment] subclass.
 */
class PlaceVisitedFragment : AbstractFragment() {

    private lateinit var binding: FragmentPlaceVisitedBinding
    private val visitModel: VisitViewModel by activityViewModels()
    private lateinit var personId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            personId = getString(Const.PERSON_ID)!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentPlaceVisitedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitModel.loadPlaceVisited(personId)
        binding.placesCount  = 0

        visitModel.repoResults.observe(viewLifecycleOwner, Observer {
            it?.apply {
                if (second is Results.Success) {
                    val visits = visitModel.placesVisited.value
                    binding.placesCount = visits?.size ?: 0
                    initTable(visits)
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
                RowHeader((indexOfFirst { it.id == t.id }+ 1).toString())
            } as ArrayList<RowHeader>

            val cells = visits.map {
                arrayListOf(
                    Cell(it.place?.name?:"No Name"),
                    Cell(it.place.toString()),
                    Cell(it.temperature?:"Unknown"),
                    Cell(it.time)
                )
            }
            val tableAdapter = PlaceVisitedTableAdapter()
            place_visited_table.setAdapter(tableAdapter)
            tableAdapter.setAllItems(columnHeader,rowHeader,cells)

        }
    }
}
