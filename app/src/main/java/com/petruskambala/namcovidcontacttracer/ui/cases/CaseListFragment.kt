package com.petruskambala.namcovidcontacttracer.ui.cases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment

/**
 * A simple [Fragment] subclass.
 */
class CaseListFragment : AbstractFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_case_list, container, false)
    }

}
