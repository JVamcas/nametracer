package com.pet001kambala.namecontacttracer.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pet001kambala.namecontacttracer.MainActivity
import com.pet001kambala.namecontacttracer.R
import com.pet001kambala.namecontacttracer.databinding.FragmentUpdateProfileBinding
import com.pet001kambala.namecontacttracer.model.Gender
import com.pet001kambala.namecontacttracer.model.Person
import com.pet001kambala.namecontacttracer.ui.AbstractFragment
import com.pet001kambala.namecontacttracer.ui.ObserveOnce
import com.pet001kambala.namecontacttracer.utils.Const
import com.pet001kambala.namecontacttracer.utils.ParseUtil
import com.pet001kambala.namecontacttracer.utils.Results
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_update_profile.*
import kotlinx.android.synthetic.main.user_icon_layout.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : AbstractFragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        accountModel.currentAccount.observe(viewLifecycleOwner, Observer { account ->
            account?.let {
                val copy = ParseUtil.copyOf(
                    if (account is Person) account else Person(account = account),
                    Person::class.java
                )
                binding.account = copy
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gender.apply {
            setAdapter(ArrayAdapter(
                requireContext(), R.layout.account_select_auto_layout,
                Gender.values().map { it.name }
            ))
        }
        account_type.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.account_select_auto_layout,
                    arrayListOf("PERSONAL", "POINT OF CONTACT")
                )
            )
        }

        birth_date.setOnClickListener { selectDate { birth_date.setText(it) } }

        update_account.setOnClickListener {
            update_account.isEnabled = false
            showProgressBar("Updating your profile...")
            val modelId = accountModel.currentAccount.value!!.id
            accountModel.updateAccount(binding.account!!.also { it.id = modelId })
        }
        accountModel.repoResults.observe(viewLifecycleOwner, ObserveOnce {
            it.apply {
                endProgressBar()
                update_account.isEnabled = true
                val modelId = accountModel.currentAccount.value!!.id
                if (it.second is Results.Success) {
                    renameTempImageFile(Const.IMAGE_ROOT_PATH, Const.TEMP_FILE, modelId)
                    accountModel.currentAccount.value?.let { it.photoUrl = "" }
                    navController.popBackStack()
                }
                super.parseRepoResults(it.second, "Profile")
            }
        })

        take_new_picture.setOnClickListener {
            val currentUser = binding.account!!
            val filePath = ParseUtil.iconPath(Const.IMAGE_ROOT_PATH, Const.TEMP_FILE)
            takePicture(currentUser, filePath)
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = (activity as MainActivity)
        activity.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackClick() {
        showWarningDialog(Const.DISCARD_CHANGES, object : WarningDialogListener {
            /***
             * Called when user Ok the delete Op
             */
            override fun onOkWarning() {
                deleteFile(requireActivity(), Const.IMAGE_ROOT_PATH, Const.TEMP_FILE)
                super@UpdateProfileFragment.onBackClick()
            }

            /***
             * Called when user explicitly cancelled the op or when dialog dismissed
             * by touching elsewhere in the device screen
             */
            override fun onCancelWarning() {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle the up button here
        if (item.itemId == android.R.id.home) {
            onBackClick()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
