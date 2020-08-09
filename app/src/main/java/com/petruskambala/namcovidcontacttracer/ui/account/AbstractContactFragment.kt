package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentPlaceVisitedBinding
import com.petruskambala.namcovidcontacttracer.model.Cell
import com.petruskambala.namcovidcontacttracer.model.ColumnHeader
import com.petruskambala.namcovidcontacttracer.model.RowHeader
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.visit.VisitViewModel
import kotlinx.android.synthetic.main.fragment_place_visited.*

abstract class AbstractContactFragment: AbstractFragment() {


    lateinit var binding: FragmentPlaceVisitedBinding
    val visitModel: VisitViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentPlaceVisitedBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun initTable(
        colHeader: ArrayList<ColumnHeader>,
        rows: List<ArrayList<Cell>>,
        rowHeader: ArrayList<RowHeader>
    ) {
        val tableAdapter =
            PlaceVisitedTableAdapter()
        place_visited_table.setAdapter(tableAdapter)
        tableAdapter.setAllItems(colHeader, rowHeader, rows)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.travel_history, menu)
    }
}