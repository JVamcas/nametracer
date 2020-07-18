package com.petruskambala.namcovidcontacttracer.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object{
        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        var ICON_PATH_PATTERN = ".*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*"

        fun today(): String {
            return SimpleDateFormat(DATE_FORMAT, Locale.US)
                .format(Date())
        }

        fun parseDate(date: String?): Date? {
            try {
                return SimpleDateFormat(DATE_FORMAT, Locale.US).parse(date)
            } catch (ignore: ParseException) {
            }
            return null
        }

        fun getDate(dateStr: String): String? {
            return dateStr.replace(".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex(), "$1")
        }

        fun getTime(dateStr: String): String? {
            return dateStr.replace(".*(\\d{2}:\\d{2}:\\d{2}).*".toRegex(), "$1")
        }
        fun isToday(dateStr: String): Boolean{
            val date = getDate(dateStr)
            val today = getDate(today())
            return date == today
        }
    }
}