package com.petruskambala.namcovidcontacttracer.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.ProgressbarBinding
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthState
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthenticationViewModel
import com.petruskambala.namcovidcontacttracer.utils.Results
import com.petruskambala.namcovidcontacttracer.utils.Results.Error.CODE.*
import com.petruskambala.namcovidcontacttracer.utils.Results.Success.CODE.*
import kotlinx.android.synthetic.main.content_main.*

abstract class AbstractFragment : Fragment() {
    val authModel: AuthenticationViewModel by activityViewModels()
    private lateinit var mDialog: Dialog
    lateinit var navController: NavController
    private lateinit var mProgressbarBinding: ProgressbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authModel.authState.observe(viewLifecycleOwner, Observer { authState ->
            val currentDest = navController.currentDestination?.id
            if (authState != AuthState.AUTHENTICATED) {
                if (currentDest == R.id.loginFragment || currentDest == R.id.registrationFragment)
                    return@Observer
                navController.navigate(R.id.action_global_loginFragment)
            }
        })
    }

    protected open fun showProgressBar(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        mProgressbarBinding = ProgressbarBinding.inflate(layoutInflater, null, false)
        mProgressbarBinding.progressMsg.text = message
        builder.setView(mProgressbarBinding.root)

        mDialog = builder.create().apply {
            setCancelable(false)
            show()
        }
    }

    protected fun updateProgressBarMsg(msg: String) {
        mProgressbarBinding.progressMsg.text = msg
    }

    protected open fun endProgressBar() {
        mDialog.cancel()
    }

    fun showToast(msg: String?) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
    }

    protected fun parseRepoResults(mResults: Results?, modelName: String) {
        if (mResults is Results.Success) {
            when (mResults.code) {
                AUTH_SUCCESS -> showToast("Welcome to Covid-19 Contact Tracer!")
                WRITE_SUCCESS -> showToast("$modelName registered successfully.")
                UPDATE_SUCCESS -> showToast("$modelName updated successfully.")
                LOGOUT_SUCCESS -> showToast("Logout successfully!")
                DELETE_SUCCESS -> showToast("$modelName deleted successfully.")
                LOAD_SUCCESS -> showToast("")
            }
        } else if (mResults is Results.Error) {
            when (mResults.code) {
                PERMISSION_DENIED -> showToast("Err: Permission denied!")
                NETWORK -> showToast("Err: No internet connection!")
                ENTITY_EXISTS -> showToast("Err: $modelName is already registered!")
                AUTH -> showToast("Err: Invalid login details.")
                NO_RECORD -> showToast("Err: No record found for your search.")
                NO_ACCOUNT -> showToast("Err: Visitor has no account.")
                else -> showToast("Err: Unknown error!")
            }
        }
    }
}