package com.petruskambala.namcovidcontacttracer.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petruskambala.namcovidcontacttracer.R

/**
 * A simple [Fragment] subclass.
 */
class PlaceVisitorsFragment : PlaceVisitedFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_visitors, container, false)
    }

}
