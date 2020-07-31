package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.model.Cell
import com.petruskambala.namcovidcontacttracer.model.ColumnHeader
import com.petruskambala.namcovidcontacttracer.model.RowHeader
import com.petruskambala.namcovidcontacttracer.ui.AbstractViewModel
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.Results
import jxl.Workbook
import java.io.File

/**
 * A [Fragment] subclass that loads and displays information of visitors for
 * who visited a specific place within the last 14 days
 */
class VisitorsListFragment : PlaceVisitedFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(Const.PERSON_ID)?.let { visitModel.switchPlace(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        visitModel.visitorsList.observe(viewLifecycleOwner, Observer {
            binding.placesCount = it?.size ?: 0
            if (it == null)

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
            when (it.first) {
                AbstractViewModel.LoadState.LOADING -> showProgressBar("Loading visitors...")
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
        return true
    }
}
