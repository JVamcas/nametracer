package com.petruskambala.nametracer.ui.visit

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.petruskambala.nametracer.R
import com.petruskambala.nametracer.model.Cell
import com.petruskambala.nametracer.model.ColumnHeader
import com.petruskambala.nametracer.model.RowHeader
import com.petruskambala.nametracer.ui.AbstractViewModel
import com.petruskambala.nametracer.ui.account.AbstractContactFragment
import com.petruskambala.nametracer.utils.Const
import jxl.Workbook
import kotlinx.android.synthetic.main.fragment_place_visited.*
import java.io.File

/**
 * A [PlaceVisitedFragment] subclass that loads and displays information of visitors
 * who visited a specific place within the last 14 days
 */
class VisitorsListFragment : AbstractContactFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(Const.PERSON_ID)?.let { visitModel.switchPlace(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitModel.visitorsList.observe(viewLifecycleOwner, Observer {
            binding.placesCount = it?.size ?: 0
            no_places.text = if (it == null) "No suspected contacts." else null

            it?.apply {
                val visit = first()
                val colHeader = visit.personColumns.map { ColumnHeader(it) } as ArrayList
                val rows = map { it.personData.map { Cell(it ?: "Unknown") } as ArrayList }
                val rowHeader = map { t ->
                    RowHeader((indexOfFirst { it.id == t.id } + 1).toString())
                } as ArrayList<RowHeader>
                initTable(colHeader, rows, rowHeader)
            }
        })

        visitModel.modelLoadState.observe(viewLifecycleOwner, Observer {
            when (it?.first) {
                AbstractViewModel.LoadState.LOADING -> showProgressBar("Loading suspected contacts...")
                AbstractViewModel.LoadState.LOADED -> {
                    endProgressBar()
//                    if (it.second is Results.Error)
//                        parseRepoResults(it.second, "")
                }
                AbstractViewModel.LoadState.NO_LOAD -> {
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.export_to_excel) {
            if (isStoragePermissionGranted()) {//permission must have been granted
                getStorageDir { path ->
                    path?.let {
                        visitModel.visitorsList.value?.apply {
                            val file = File(path, "${first().place?.name} Visit Record.xlsx")
                            exportPlaceVisits(Workbook.createWorkbook(file), this)
                            showToast("Saved to: ${file.absolutePath}")
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
