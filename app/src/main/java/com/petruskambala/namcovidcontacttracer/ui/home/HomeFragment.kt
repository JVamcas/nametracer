package com.petruskambala.namcovidcontacttracer.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.FragmentHomeBinding
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthState
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : AbstractFragment() {

    private lateinit var binding: FragmentHomeBinding

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

        authModel.currentAccount.observe(viewLifecycleOwner, Observer {
            it?.let { binding.account = it }
        })

        record_visit.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_recordVisitFragment)
        }
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
