package com.petruskambala.namcovidcontacttracer.utils

import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.ui.AbstractAdapter
import java.util.*
import kotlin.collections.ArrayList

abstract class AbstractModelFilter<T : AbstractModel, K : RecyclerView.ViewHolder>(
    private val mAdapter: AbstractAdapter<T, K>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults? {
        val results = FilterResults()
        if (constraint?.length != null && constraint.isNotEmpty()) {
            val filteredModel = ArrayList<T>()
//            for (model in mAdapter.modelList) {
//                if (model.name.toLowerCase(Locale.ROOT).contains(constraint)) {
//                    filteredModel.add(model)
//                }
//            }
            results.count = filteredModel.size
            results.values = filteredModel
        } else {
            results.count = mAdapter.modelList.size
            results.values = mAdapter.modelList
        }
        return results
    }
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        mAdapter.modelList = results.values as ArrayList<T>
    }
}