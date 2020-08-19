package com.pet001kambala.namecontacttracer.ui.cases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.CaseViewBinding
import com.pet001kambala.namecontacttracer.model.Cases
import com.pet001kambala.namecontacttracer.ui.AbstractAdapter
import com.pet001kambala.namecontacttracer.utils.AbstractModelFilter

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