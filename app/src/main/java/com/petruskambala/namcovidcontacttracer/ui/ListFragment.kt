package com.petruskambala.namcovidcontacttracer.ui

import androidx.recyclerview.widget.RecyclerView
import com.petruskambala.namcovidcontacttracer.model.AbstractModel

interface ListFragment<K : AbstractModel, T : RecyclerView.ViewHolder> :
    AbstractAdapter.ModelViewClickListener<K> {

     fun handleRecycleView(recyclerView: RecyclerView, mListener: AbstractAdapter.ModelViewClickListener<K>
    )
}