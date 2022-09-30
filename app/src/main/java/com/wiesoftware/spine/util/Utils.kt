package com.wiesoftware.spine.util

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Utils {

    companion object{

        fun showToast(context: Context,msg:String){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }

        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun isValidPassword(password: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(password)
            return matcher.matches()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun dateformat(date:String): LocalDate {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(date, formatter)
            return date
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun dateTimeformat(date:String): LocalDate {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm", Locale.ENGLISH)
            val date: LocalDate = LocalDate.parse(date, formatter)
            return date
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun dateTimeformat(date: LocalDate): String {
            val formatter: DateTimeFormatter =
                DateTimeFormatter.ofPattern("hh:mm", Locale.ENGLISH)
            return formatter.format(date)
        }
    }
}