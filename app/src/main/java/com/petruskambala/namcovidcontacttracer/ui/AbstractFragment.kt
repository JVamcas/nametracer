package com.petruskambala.namcovidcontacttracer.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.AppDismissDialogBinding
import com.petruskambala.namcovidcontacttracer.databinding.ProgressbarBinding
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthState
import com.petruskambala.namcovidcontacttracer.ui.authentication.AuthenticationViewModel
import com.petruskambala.namcovidcontacttracer.utils.AccessType
import com.petruskambala.namcovidcontacttracer.utils.Results
import com.petruskambala.namcovidcontacttracer.utils.Results.Error.CODE.*
import com.petruskambala.namcovidcontacttracer.utils.Results.Success.CODE.*
import jxl.write.Label
import jxl.write.WritableWorkbook
import lib.folderpicker.FolderPicker


abstract class AbstractFragment : Fragment() {
    val authModel: AuthenticationViewModel by activityViewModels()
    private var mDialog: Dialog? = null
    lateinit var navController: NavController
    private lateinit var mProgressbarBinding: ProgressbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackClicks()

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

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun exportPersonTravelHistory(wkb: WritableWorkbook, visitList: ArrayList<Visit>) {
        val visit = visitList.first()
        val person = visit.person

        wkb.createSheet("${person?.name} Travel History", 0).apply {
            var rowIndex = 0
            addCell(Label(0, rowIndex, "PERSONAL DETAILS"))
            mergeCells(0, rowIndex, 6, rowIndex)

            visit.apply { //person column header
                var colIndex = 0
                rowIndex += 2
                personColumns.forEach { addCell(Label(colIndex++, rowIndex, it)) }

                rowIndex++ //person details
                colIndex = 0
                personData.forEach { addCell(Label(colIndex++, rowIndex, it)) }

                rowIndex += 2
                addCell(Label(0, rowIndex, "TRAVEL HISTORY DETAILS"))
                mergeCells(0, rowIndex, 3, rowIndex)

                colIndex = 0 //place columns headers
                rowIndex++
                placeColumns.forEach { addCell(Label(colIndex++, rowIndex, it)) }
            }
            visitList.forEach {
                var colIndex = 0
                rowIndex++
                it.placeData.forEach { addCell(Label(colIndex++, rowIndex, it)) }
            }
        }
        wkb.write()
        wkb.close()
    }

    fun exportPlaceVisits(wkb: WritableWorkbook, visitList: ArrayList<Visit>) {
        val visit = visitList.first()
        val place = visit.place

        wkb.createSheet("${place?.name} Visit History", 0).apply {
            var rowIndex = 0
            addCell(Label(0, rowIndex, "VISITORS DETAILS"))
            mergeCells(0, rowIndex, 3, rowIndex)

            visit.apply {//person column header
                var colIndex = 0
                rowIndex += 2
                personColumns.forEach { addCell(Label(colIndex++, rowIndex, it)) }

                visitList.forEach {//visitors details
                    colIndex = 0
                    rowIndex++
                    it.placeData.forEach { addCell(Label(colIndex++, rowIndex, it)) }
                }
            }
        }
        wkb.write()
        wkb.close()
    }

    private lateinit var callback: (String?) -> Unit
    private val SELECT_DIR = 0
    fun getStorageDir(callback: (path: String?) -> Unit) {
        this.callback = callback

        val intent = Intent(requireContext(), FolderPicker::class.java)
        intent.putExtra("title", "Select folder");
        startActivityForResult(intent, SELECT_DIR)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == SELECT_DIR) {
            data?.apply {
                val folderLocation: String? = extras?.getString("data")
                callback(folderLocation)
            }
        }
    }


    protected open fun endProgressBar() {
        mDialog?.cancel()
    }

    fun validateOp(accessType: AccessType): Boolean {
        return true
//        authModel.currentAccount.value?.let {
//            return accessType in it.permission!!
//        }
//        return false
    }

    protected fun showExitDialog() {
        with(AppDismissDialogBinding.inflate(layoutInflater, null, false)) {
            val dialog: AlertDialog = AlertDialog.Builder(requireContext()).let {
                it.setView(root)
                it.create()
            }.apply {
                setCancelable(false)
                show()
            }
            okExitBtn.setOnClickListener {
                dialog.dismiss()
                requireActivity().finish()
            }
            cancelExitBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun handleBackClicks() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackClick()
                }
            })
    }

    protected open fun onBackClick() {
        navController.popBackStack()
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