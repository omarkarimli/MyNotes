package com.omarkarimli.mynotes

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    // Long timestamp to formatted date string
    fun convertLongToTimeString(timeInMillis: Long, format: String = "dd/MM/yyyy"): String {
        val date = Date(timeInMillis)
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(date)
    }

    // Date string to Long timestamp
    fun convertTimeStringToLong(dateString: String, format: String = "dd/MM/yyyy"): Long {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        val date = formatter.parse(dateString) // Convert the string to a Date object
        return date?.time ?: 0L  // Return the time in milliseconds or 0 if parsing fails
    }
}