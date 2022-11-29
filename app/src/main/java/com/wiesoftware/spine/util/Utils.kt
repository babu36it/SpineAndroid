package com.wiesoftware.spine.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.cometchat.pro.uikit.R
import utils.Utils
import java.io.File
import java.text.SimpleDateFormat
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

        fun getOutputMediaFile(context: Context): String? {
            val var0 = File(Environment.getExternalStorageDirectory(), context.resources.getString(R.string.app_name))
            return if (!var0.exists() && !var0.mkdirs()) {
                null
            } else {
                val var1 = (Environment.getExternalStorageDirectory().toString() + "/" + context.resources.getString(
                    R.string.app_name) + "/"
                        + "audio/")
                createDirectory(var1)
                var1 + SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".mp3"
            }
        }

        fun createDirectory(var0: String?) {
            if (!File(var0).exists()) {
                File(var0).mkdirs()
            }
        }
    }
}