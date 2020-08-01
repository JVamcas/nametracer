package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
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
 * A simple [Fragment] subclass.
 */
class PlaceVisitedFragment : AbstractContactFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(Const.PERSON_ID)?.let { visitModel.switchPerson(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placesCount = 0
        visitModel.personVisits.observe(viewLifecycleOwner, Observer {
            binding.placesCount = it?.size ?: 0

            it?.apply {
                val visit = first()
                val colHeader = visit.placeColumns.map { ColumnHeader(it) } as ArrayList
                val rows = map { it.placeData.map { Cell(it ?: "Unknown") } as ArrayList }
                val rowHeader = map { t ->
                    RowHeader((indexOfFirst { it.id == t.id } + 1).toString())
                } as ArrayList<RowHeader>
                initTable(colHeader, rows, rowHeader)
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
