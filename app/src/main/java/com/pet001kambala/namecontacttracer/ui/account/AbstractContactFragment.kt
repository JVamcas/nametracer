package com.pet001kambala.namecontacttracer.ui.account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentPlaceVisitedBinding
import com.pet001kambala.namecontacttracer.model.Cell
import com.pet001kambala.namecontacttracer.model.ColumnHeader
import com.pet001kambala.namecontacttracer.model.RowHeader
import com.pet001kambala.namecontacttracer.ui.AbstractFragment
import com.pet001kambala.namecontacttracer.ui.visit.PlaceVisitedTableAdapter
import com.pet001kambala.namecontacttracer.ui.visit.VisitViewModel
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