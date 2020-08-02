package com.petruskambala.namcovidcontacttracer.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseMethod
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.petruskambala.namcovidcontacttracer.model.AccountType
import com.petruskambala.namcovidcontacttracer.model.CaseState
import com.petruskambala.namcovidcontacttracer.model.Gender
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidEmail
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidMobile
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidNationalID
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidTemperature
import kotlinx.android.synthetic.main.fragment_new_case.*

class BindingUtil {
    companion object {

        @JvmStatic
        @BindingAdapter(value = ["cellphone", "email", "reqMail", "reqCell", "name", "country"])
        fun validateUserProfileUpdate(
            mButton: MaterialButton,
            cell: String?,
            mail: String?,
            reqMail: Boolean,
            reqCell: Boolean,
            name: String?,
            country: String?
        ) {
            mButton.isEnabled =
                !name.isNullOrEmpty() && !country.isNullOrEmpty()
                        && reqCell && isValidMobile(cell)
                        || reqMail && isValidEmail(mail)
        }

        @JvmStatic
        @BindingAdapter(value = ["email", "cellphone"])
        fun validateEmailCell(mEditText: EditText, cellphone: String?, email: String?) {
            mEditText.error = if (cellphone.isNullOrEmpty() && email.isNullOrEmpty())
                "Provide either email or cellphone number." else null
        }

        @JvmStatic
        @BindingAdapter(value = ["password", "confirmPassword"])
        fun confirmPassword(mEditText: EditText, password: String?, confirmPassword: String?) {
            mEditText.error =
                if (confirmPassword.isNullOrEmpty() || password != confirmPassword) "Passwords do not match." else null
            println("password match ${password == confirmPassword}")
        }

        @JvmStatic
        @BindingAdapter(value = ["password"])
        fun validatePassword(mEditText: EditText, password: String?) {
            mEditText.error =
                if (password.isNullOrEmpty() || password.length < 8) "Password should be at least 8 characters long." else null
        }


        @JvmStatic
        @BindingAdapter(value = ["errorMsg", "editContent"])
        fun emptyEdit(mEditText: EditText, errorMsg: String?, value: String?) {
            mEditText.error = if (value.isNullOrEmpty()) errorMsg
            else null
        }

        /***
         * Load image from provided url, transform it and set it into the imageview
         * @param mView the image view
         * @param default_icon default icon in case of error or when url is null
         * @param photoUrl the url: a base dir for device images else full url for online
         * @param size the required size of the image
         */
        @JvmStatic
        @BindingAdapter(value = ["default_icon", "photoUrl", "size"])
        fun loadImage(
            mView: ImageView,
            @IdRes default_icon: Int,
            photoUrl: String?,
            size: Int
        ) {
            val creator =
                ImageUtil.requestCreator(CircleTransformation, photoUrl, size, default_icon)
            creator.into(mView)
        }

        @JvmStatic
        @BindingAdapter(value = ["errorMsg", "idMailCell"])
        fun validateIDMailCell(
            mEditText: TextInputEditText,
            errorMsg: String?,
            idMailCell: String?
        ) {

            mEditText.error =
                if (isValidMobile(idMailCell) || isValidEmail(idMailCell) /**|| isValidNationalID(
                        idMailCell
                    )**/
                )
                    null else errorMsg
        }

        @JvmStatic
        @BindingAdapter(value = ["emailAddress", "cellphone", "isEmail"])
        fun validateEmailCell(
            mEditText: TextInputEditText,
            emailAddress: String?,
            cellphone: String?,
            isEmail: Boolean = false
        ) {

            mEditText.error =
                if (!isValidMobile(cellphone) && !isValidEmail(emailAddress))
                    "Enter a valid email address or cellphone number."
                else if (isEmail && !isValidEmail(emailAddress) && !emailAddress.isNullOrEmpty())
                    "Enter valid email address or leave field empty."
                else if (!isEmail && !isValidMobile(cellphone) && !cellphone.isNullOrEmpty())
                    "Enter valid cellphone number or leave field empty."
                else null
        }


        @JvmStatic
        @BindingAdapter(
            value = ["accountName", "accountType", "address",
                "emailAddress", "cellphone", "town", "password", "confirmPassword"],
            requireAll = false
        )
        fun validateBusinessAccount(
            mButton: MaterialButton,
            accountName: String?,
            accountType: String?,
            address: String?,
            emailAddress: String?,
            cellphone: String?,
            town: String?,
            password: String?,
            confirmPassword: String?
        ) {
            mButton.isEnabled = true

            mButton.isEnabled = AccountType.values()
                .map { it.name }.contains(accountType)
                    && ((isValidMobile(cellphone) && isValidEmail(emailAddress)
                    || (isValidMobile(cellphone) && emailAddress.isNullOrEmpty())
                    || (isValidEmail(emailAddress) && cellphone.isNullOrEmpty())))
                    && (!password.isNullOrEmpty() && password.length >= 8 && password == confirmPassword)
                    && !listOf(accountName, address, town).any { it.isNullOrEmpty() }
        }

        @JvmStatic
        @BindingAdapter(value = ["accountName", "accountType", "address", "town", "birthDate"])
        fun validateAccountUpdate(
            mButton: MaterialButton,
            accountName: String?,
            accountType: AccountType?,
            address: String?,
            town: String?,
            birthDate: String?
        ) {
            mButton.isEnabled =
                (accountType == AccountType.PERSONAL
                        && !listOf(
                    accountName,
                    address,
                    town,
                    birthDate
                ).any { it.isNullOrEmpty() })

                        || (accountType == AccountType.BUSINESS &&
                        !listOf(
                            accountName,
                            address,
                            town
                        ).any { it.isNullOrEmpty() })

        }

        @JvmStatic
        @BindingAdapter(value = ["birthDate", "gender"], requireAll = false)
        fun validatePersonalAccount(
            mButton: MaterialButton,
            birthDate: String?,
            gender: Gender?
        ) {
            mButton.isEnabled = !birthDate.isNullOrEmpty()
                    && gender in Gender.values()
        }

        @JvmStatic
        @BindingAdapter(value = ["email_cell_id", "mButton"])
        fun validateAuthDetails(
            mEditText: EditText,
            email_cell_id: String?,
            mButton: MaterialButton
        ) {
            email_cell_id?.apply {
                mEditText.error =
                    if (isValidMobile(email_cell_id) || isValidEmail(email_cell_id) /**|| isValidNationalID(
                            email_cell_id
                        )**/
                    ) null
                    else "Enter a valid ID, email or cellphone number."
                mButton.isEnabled =
                    (isValidMobile(email_cell_id) || isValidEmail(email_cell_id) /**|| isValidNationalID(
                        email_cell_id
                    )**/)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["userId"])
        fun validateNationalId(mEditText: EditText, nationalId: String?) {
            mEditText.error =
                if (nationalId.isNullOrEmpty() || nationalId.length != 11) "Invalid ID number." else null
        }

        @InverseMethod("toAccountType")
        @JvmStatic
        fun fromAccountType(accountType: AccountType?): String? {
            return if (accountType == AccountType.BUSINESS) "POINT OF CONTACT" else accountType?.name
        }

        @JvmStatic
        fun toAccountType(type: String?): AccountType? {
            return when (type) {
                "POINT OF CONTACT" -> AccountType.BUSINESS
                "PERSONAL" -> AccountType.PERSONAL
                else -> null
            }
        }

        @InverseMethod("toGender")
        @JvmStatic
        fun fromGender(gender: Gender?): String? {
            return gender?.name
        }

        @JvmStatic
        fun toGender(type: String): Gender? {
            return if (type !in Gender.values()
                    .map { it.name }
            ) null else Gender.valueOf(type)
        }

        @JvmStatic
        @BindingAdapter(value = ["visitorIdMailCell", "visitorTemp"], requireAll = false)
        fun validateVisitRecording(
            mButton: MaterialButton,
            idMailCell: String?,
            temperature: String?
        ) {
            mButton.isEnabled =
                (isValidEmail(idMailCell) || isValidMobile(
                    idMailCell
                )) && isValidTemperature(temperature)
        }

        @JvmStatic
        @BindingAdapter(value = ["password", "idMailCell"], requireAll = false)
        fun isValidLogin(mButton: MaterialButton, password: String?, idMailCell: String?) {
            mButton.isEnabled =
                (password?.length ?: 0 >= 8 && isValidEmail(idMailCell) || isValidMobile(
                    idMailCell
                ) || idMailCell?.length ?: 0 == 11)
        }

        @InverseMethod("toCaseState")
        @JvmStatic
        fun fromCaseState(state: CaseState?): String? {
            return state?.name
        }

        @JvmStatic
        fun toCaseState(type: String): CaseState? {
            return if (type !in CaseState.values()
                    .map { it.name }
            ) null else CaseState.valueOf(type)
        }
    }


    abstract class TextChangeLister : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }
}