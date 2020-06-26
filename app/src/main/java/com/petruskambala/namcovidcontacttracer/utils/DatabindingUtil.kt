package com.petruskambala.namcovidcontacttracer.utils

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidEmail
import com.petruskambala.namcovidcontacttracer.utils.ParseUtil.Companion.isValidMobile

class DatabindingUtil {

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
        @BindingAdapter(value = ["android:errorMsg"])
        fun emptyEdit(mEditText: EditText, errorMsg: String?) {
            mEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(value: CharSequence, i: Int, i1: Int, i2: Int) {
                    mEditText.setSelection(value.length)
                    if (TextUtils.isEmpty(value.toString())) mEditText.error =
                        errorMsg else mEditText.error = null
                }

                override fun afterTextChanged(editable: Editable) {}
            })
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
        @BindingAdapter(value = ["errorMsg", "reqCellMail"])
        fun validateMailCell(
            mEditText: TextInputEditText,
            errorMsg: String?,
            reqCellMail: Boolean
        ) {
            mEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                    val text = charSequence.toString()
                    mEditText.error =
                        if (reqCellMail && !isValidMobile(text) && !isValidEmail(text))
                            errorMsg else null
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
    }
}
