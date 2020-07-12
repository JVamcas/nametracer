package com.petruskambala.namcovidcontacttracer.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.petruskambala.namcovidcontacttracer.model.AbstractModel
import java.io.File
import java.util.regex.Pattern

class ParseUtil {

    companion object {
        fun path(vararg param: String): String {
            val path = StringBuilder()

            for (p in param)
                path.append(p).append("/")
            return path.toString()
        }

        fun <K : AbstractModel?> modelCrone(model: K, kClass: Class<K>): K {
            val json = ParseUtil.toJson(model)
            return fromJson(json, kClass)
        }
        fun <K> toJson(obj: K): String? {
            return Gson().toJson(obj)
        }

        fun <T> fromJson(json: String?, tClass: Class<T>): T {
            return Gson().fromJson(json, tClass)
        }

        fun findFilePath(mContext: Context, filePath: String): String? {
            val file = File(mContext.getExternalFilesDir(null), filePath)
            return if (file.exists()) "file:" + file.absolutePath else null
        }

        fun initDirs(mContext: Context, vararg param: String) {
            //create dir for storing profile pictures
            for (dir in param) {
                val parentDir = File(mContext.getExternalFilesDir(null), dir)
                if (!parentDir.exists()) parentDir.mkdirs()
            }
        }
        fun isValidNationalID(value: String?): Boolean{
            return value?.length?:0 == 11
        }

        /***
         * Compute relative path for the view's icon
         * @param rtDir the base dir for the icon
         * @param viewId id of the view
         * @return the relative path
         */
        fun iconPath(rtDir: String?, viewId: String): String {
            return StringBuilder(rtDir?:"").append("/_").append(viewId).append("_.jpg").toString()
        }

        fun isValidEmail(email: String?): Boolean {
            if(email.isNullOrEmpty())return false
            val email1 = email.replace("\\s+".toRegex(), "")
            val EMAIL_STRING = ("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            return !TextUtils.isEmpty(email1) && Pattern.compile(EMAIL_STRING)
                .matcher(email1).matches()
        }
        fun isValidMobile(phone: String?): Boolean {
            if (phone.isNullOrEmpty()) return false
            var phone1 = phone
            phone1 = phone1.replace("\\s+".toRegex(), "")
            return if (!TextUtils.isEmpty(phone) && !Pattern.matches(
                    "\\+?[a-zA-Z]+",
                    phone
                )
            ) {
                phone1.length in 10..13
            } else false
        }
    }
}