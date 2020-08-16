package com.petruskambala.nametracer.ui.cases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.petruskambala.nametracer.R
import com.petruskambala.nametracer.databinding.CaseViewBinding
import com.petruskambala.nametracer.model.Cases
import com.petruskambala.nametracer.ui.AbstractAdapter
import com.petruskambala.nametracer.utils.AbstractModelFilter

class CaseListAdapter(
    mListener: ModelViewClickListener<Cases>
) : AbstractAdapter<Cases, CaseListAdapter.ViewHolder>(mListener) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var cases: Cases
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CaseViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.case_view,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun getFilter(): Filter {
        return object :AbstractModelFilter<Cases, ViewHolder>(this){}
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cases = get(position)
        val binding = DataBindingUtil.getBinding<CaseViewBinding>(holder.itemView)
        binding!!.covidCase = holder.cases
        binding.root.setOnClickListener {
            mListener.onModelClick(holder.cases)
        }
    }
}