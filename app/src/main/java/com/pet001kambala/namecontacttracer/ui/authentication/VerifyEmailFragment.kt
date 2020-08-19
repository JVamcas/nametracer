package com.pet001kambala.namecontacttracer.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.ui.ObserveOnce
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
            accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
                it.apply {
                    endProgressBar()
                    re_send_link.isEnabled = true
                    parseRepoResults(second, "")
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle the up button here
        if (item.itemId == android.R.id.home) {
            onBackClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackClick() {
       navController.popBackStack(R.id.selectLoginModeFragment,false)
    }
}
