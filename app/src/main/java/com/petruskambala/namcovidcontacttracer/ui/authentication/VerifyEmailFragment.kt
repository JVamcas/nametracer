package com.petruskambala.namcovidcontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.petruskambala.namcovidcontacttracer.MainActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.ui.AbstractFragment
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_verify_email.*

/**
 * A simple [Fragment] subclass to handle email address verification.
 */
class VerifyEmailFragment : AbstractAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        re_send_link.setOnClickListener {
            re_send_link.isEnabled = false
            verifyEmail()
            accountModel.repoResults.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    endProgressBar()
                    re_send_link.isEnabled = true
                    parseRepoResults(second, "")
                    accountModel.clearRepoResults(viewLifecycleOwner)
                }
            })
        }
    }
}
