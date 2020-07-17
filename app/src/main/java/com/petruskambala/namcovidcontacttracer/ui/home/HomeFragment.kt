package com.petruskambala.namcovidcontacttracer.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentHomeBinding
import com.petruskambala.namcovidcontacttracer.model.CaseState
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.cases.CaseViewModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : AbstractFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val caseModel: CaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        caseModel.caseList.observe(viewLifecycleOwner, Observer {
            it?.apply {
                it.count { case -> case.inQuarantine }

                val recovered = arrayListOf(
                    BarEntry(
                        0f,
                        it.count { case -> case.caseState == CaseState.RECOVERED }.toFloat()
                    )
                )
                val active = arrayListOf(
                    BarEntry(
                        1f,
                        it.count { case -> case.caseState == CaseState.ACTIVE }.toFloat()
                    )
                )
                val deaths = arrayListOf(
                    BarEntry(
                        2f,
                        it.count { case -> case.caseState == CaseState.DEAD }.toFloat()
                    )
                )
                val inQuarantine =
                    arrayListOf(BarEntry(3f, it.count { case -> case.inQuarantine }.toFloat()))

                val total = arrayListOf(BarEntry(4f,it.count().toFloat()))

                val barData = BarData(
                    BarDataSet(recovered, "Recovered").apply {
                        color = Color.parseColor("#28B463")
                    },
                    BarDataSet(active, "Active").apply { color = Color.parseColor("#A93226") },
                    BarDataSet(deaths, "Deaths").apply { color = Color.parseColor("#5D6D7E") },
                    BarDataSet(inQuarantine, "In Quarantine").apply {
                        color = Color.parseColor("#1ABC9C")
                    },
                    BarDataSet(total, "Total").apply { color = Color.parseColor("#9A7D0A") }
                ).also {
                    it.setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return if(value.toInt() == 0) "" else "${value.toInt()}"
                        }
                    })
                }

                covid_stat.apply {
                    data = barData
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)
                    description.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisRight.isEnabled = false
                    xAxis.setDrawLabels(false)
                    axisLeft.axisMinimum = 0f
                    axisRight.axisMinimum = 0f
                    invalidate()
                }
            }
        })


        authModel.currentAccount.observe(viewLifecycleOwner, Observer
        {
            it?.let { binding.account = it }
        })
        record_visit.setOnClickListener { navController.navigate(R.id.action_homeFragment_to_recordVisitFragment) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        authModel.currentAccount.observe(viewLifecycleOwner, Observer {
            it?.apply {
                if (admin) {
                    super.onCreateOptionsMenu(menu, inflater)
                    inflater.inflate(R.menu.admin_menu, menu)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cases -> {
                navController.navigate(R.id.action_homeFragment_to_casesFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
