package com.petruskambala.namcovidcontacttracer.ui.cases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.CaseViewBinding
import com.petruskambala.namcovidcontacttracer.model.CovidCase
import com.petruskambala.namcovidcontacttracer.ui.AbstractAdapter
import com.petruskambala.namcovidcontacttracer.utils.AbstractModelFilter

class CaseListAdapter(
    mListener: ModelViewClickListener<CovidCase>
) : AbstractAdapter<CovidCase, CaseListAdapter.ViewHolder>(mListener) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var covidCase: CovidCase
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
        return object :AbstractModelFilter<CovidCase, ViewHolder>(this){}
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.covidCase = get(position)
        val binding = DataBindingUtil.getBinding<CaseViewBinding>(holder.itemView)
        binding!!.covidCase = holder.covidCase
        binding.root.setOnClickListener {
            mListener.onModelClick(holder.covidCase)
        }
    }
}