package com.petruskambala.namcovidcontacttracer.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentHomeBinding
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.cases.CaseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
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

        caseModel.repoResults.observe(viewLifecycleOwner, Observer {
            caseModel.covidStat.value?.apply {
                binding.stat = this
                val barData = BarData(
                    BarDataSet(listOf(BarEntry(0f, recovered.toFloat())), "Recovered").apply {
                        color = Color.parseColor("#28B463")
                    },
                    BarDataSet(listOf(BarEntry(1f, active.toFloat())), "Active").apply {
                        color = Color.parseColor("#A93226")
                    },
                    BarDataSet(listOf(BarEntry(2f, deaths.toFloat())), "Deaths").apply {
                        color = Color.parseColor("#5D6D7E")
                    },
                    BarDataSet(listOf(BarEntry(3f, total.toFloat())), "Cases").apply {
                        color = Color.parseColor("#9A7D0A")
                    },
                    BarDataSet(listOf(BarEntry(4f, tests.toFloat())), "Tested").apply {
                        color = Color.parseColor("#F68C06")
                    }
                ).also {
                    it.setValueFormatter(object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return if (value.toInt() == 0) "" else "${value.toInt()}"
                        }
                    })
                }
                covid_stat.apply {
                    data = barData
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)
                    description.isEnabled = false
                    xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
                    axisRight.isEnabled = false
                    xAxis.setDrawLabels(false)
                    axisLeft.axisMinimum = 0f
                    axisRight.axisMinimum = 0f
                    invalidate()
                }
                binding.newCaseCount =
                    if (this.newCases.toInt() == 0) "No New Cases" else "+ ${this.newCases}"
            }
        })

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer
        {
            it?.let { binding.account = it }
        })
        record_visit.setOnClickListener { navController.navigate(R.id.action_homeFragment_to_recordVisitFragment) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        accountModel.currentAccount.observe(viewLifecycleOwner, Observer {
            it?.apply {
                if (admin && accountType == AccountType.PERSONAL) {
                    super.onCreateOptionsMenu(menu, inflater)
                    inflater.inflate(R.menu.admin_menu, menu)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cases -> { navController.navigate(R.id.action_homeFragment_to_casesFragment) }
            R.id.point_of_contact ->{
                navController.navigate(R.id.action_homeFragment_to_findPointOfContactFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackClick() {
        showExitDialog()
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
