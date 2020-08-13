package com.petruskambala.namcovidcontacttracer.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.datepicker.MaterialDatePicker
import com.petruskambala.namcovidcontacttracer.CameraCaptureActivity
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.AppDismissDialogBinding
import com.petruskambala.namcovidcontacttracer.databinding.ProgressbarBinding
import com.petruskambala.namcovidcontacttracer.databinding.WarningDialogBinding
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import com.petruskambala.namcovidcontacttracer.model.Visit
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel
import com.petruskambala.namcovidcontacttracer.ui.account.AccountViewModel.AuthState.*
import com.petruskambala.namcovidcontacttracer.ui.account.UpdateProfileFragment
import com.petruskambala.namcovidcontacttracer.ui.authentication.AbstractAuthFragment
import com.petruskambala.namcovidcontacttracer.utils.*
import com.petruskambala.namcovidcontacttracer.utils.Const
import com.petruskambala.namcovidcontacttracer.utils.Results.Error.CODE.*
import com.petruskambala.namcovidcontacttracer.utils.Results.Success.CODE.*
import com.squareup.picasso.Picasso
import jxl.write.Label
import jxl.write.WritableWorkbook
import lib.folderpicker.FolderPicker
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


abstract class AbstractFragment : Fragment() {
    val accountModel: AccountViewModel by activityViewModels()
    private var mDialog: Dialog? = null
    lateinit var navController: NavController
    private lateinit var mProgressbarBinding: ProgressbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        handleBackClicks()

        accountModel.authState.observe(viewLifecycleOwner, Observer {
            it?.apply {
                if (this == UNAUTHENTICATED && this@AbstractFragment !is AbstractAuthFragment) {
                    endAuthFlow()
                    navController.popBackStack(R.id.selectLoginModeFragment, false)
                } else if (this == ACCOUNT_INFO_MISSING && this@AbstractFragment !is UpdateProfileFragment) {
                    endAuthFlow()
                    showToast("Please update your account.")
                    navController.navigate(R.id.action_global_updateProfileFragment)
                }
            }
        })
    }

    protected fun endAuthFlow() {
        endProgressBar()
        accountModel.clearRepoResults(viewLifecycleOwner)
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

        wkb.createSheet("${place?.name} Visitors Details", 0).apply {
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
                    it.personData.forEach { addCell(Label(colIndex++, rowIndex, it)) }
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

    private lateinit var model: AbstractModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_DIR) {
                data?.apply {
                    val folderLocation: String? = extras?.getString("data")
                    callback(folderLocation)
                }
            } else if (requestCode == Const.CAPTURE_PICTURE) {
                val absPath = File(
                    requireActivity().getExternalFilesDir(null),
                    data?.getStringExtra(Const.ICON_PATH)
                ).absolutePath
                model.id = Const.TEMP_FILE
                model.photoUrl = "file:$absPath" //force update of model icon
            }
        }
    }
    /***
     * Invoke camera activity to take a picture
     * @param model whose icon is to be captured
     * @param fileName abs filePath where image will be saved
     */
    fun takePicture(model: AbstractModel, fileName: String) {
        this.model = model
        val mIntent = Intent(requireActivity(), CameraCaptureActivity::class.java)
        mIntent.putExtra(Const.ICON_PATH, fileName)
        startActivityForResult(mIntent, Const.CAPTURE_PICTURE)
    }

    fun renameTempImageFile(dir: String?, from: String, to: String) {
        val sourcePath = ParseUtil.iconPath(dir, from)
        val destPath = ParseUtil.iconPath(dir, to)
        val source = File(requireActivity().getExternalFilesDir(null), sourcePath)
        if (source.exists()) {
            val dest = File(requireActivity().getExternalFilesDir(null), destPath)
            source.renameTo(dest)
        }
    }

    open fun deleteFile(activity: FragmentActivity,baseDir: String, viewId: String) {
        var filePath = ParseUtil.iconPath(baseDir, viewId)
        filePath = File(activity.getExternalFilesDir(null), filePath).absolutePath
        File(filePath).delete()
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
                AUTH_SUCCESS -> { }
                WRITE_SUCCESS -> showToast("$modelName registered successfully.")
                UPDATE_SUCCESS -> showToast("$modelName updated successfully.")
                LOGOUT_SUCCESS -> showToast("Logout successfully!")
                DELETE_SUCCESS -> showToast("$modelName deleted successfully.")
                VERIFICATION_EMAIL_SENT -> showToast("Verification email sent.")
            }
        } else if (mResults is Results.Error) {
            when (mResults.code) {
                PERMISSION_DENIED -> showToast("Err: Permission denied!")
                NETWORK -> showToast("Err: No internet connection!")
                ENTITY_EXISTS -> showToast("Err: $modelName is already registered!")
                AUTH -> showToast("Err: Authentication.")
                NO_RECORD -> showToast("Err: No record found for your search.")
                NO_ACCOUNT -> showToast("Err: No record found for your search.")
                NO_SUCH_USER -> showToast("Err: No account with such email.")
                DUPLICATE_ACCOUNT -> showToast("Err: Account already exist.")
                INCORRECT_EMAIL_PASSWORD_COMBO -> showToast("Err: Incorrect email or password.")
                INVALID_AUTH_CODE -> showToast("Err: Incorrect authentication code.")
                else -> showToast("Err: Unknown error!")
            }
        }
    }

    protected fun showWarningDialog(warningTxt: String?, mListener: WarningDialogListener) {
        with(WarningDialogBinding.inflate(layoutInflater, null, false))
        {
            val dialog = AlertDialog.Builder(requireContext()).let {
                it.setView(root)
                it.create()
            }.apply { show() }
            title.text = warningTxt
            val okBtnClicked = AtomicBoolean(false)
            okBtn.setOnClickListener {
                okBtnClicked.set(true)
                dialog.dismiss()
                mListener.onOkWarning()
            }
            cancelBtn.setOnClickListener { dialog.dismiss() }
            dialog.setOnDismissListener { if (!okBtnClicked.get()) mListener.onCancelWarning() }
        }
    }

    protected fun selectDate(callback: (date: String) -> Unit) {
        MaterialDatePicker.Builder.datePicker().apply {
            setSelection(Calendar.getInstance().timeInMillis)
            val picker = build()
            picker.show(requireActivity().supportFragmentManager, "")
            picker.addOnPositiveButtonClickListener { callback(DateUtil.parseDate(it)) }
        }
    }

    interface WarningDialogListener {
        /***
         * Called when user Ok the delete Op
         */
        fun onOkWarning()

        /***
         * Called when user explicitly cancelled the op or when dialog dismissed
         * by touching elsewhere in the device screen
         */
        fun onCancelWarning()
    }
}
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}