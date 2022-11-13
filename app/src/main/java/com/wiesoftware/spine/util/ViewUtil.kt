package com.wiesoftware.spine.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.util.*




const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


fun getFbImage(fbId:String):String{
    return "https://graph.facebook.com/${fbId}/picture?type=large"
}

private const val NAME = "Spine"
private const val MODE = Context.MODE_PRIVATE

lateinit var Prefs: SharedPreferences
fun init(context: Context) {
    if (!::Prefs.isInitialized) {Prefs = context.getSharedPreferences(NAME, MODE) }
    
}

fun SharedPreferences.putAny(name: String, any: Any) {
    when (any) {
        is String -> edit().putString(name, any).apply()
        is Int -> edit().putInt(name, any).apply()
        is Boolean -> edit().putBoolean(name,any).apply()

    }
}
fun SharedPreferences.remove(name:String){
    edit().remove(name).apply()
}

 fun getToken(): String? {
  return   Prefs.getString("AuthToken","")
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard(it) }
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
}


fun Any.toast(context: Context):Toast{
    return Toast.makeText(context, this.toString(), Toast.LENGTH_LONG).apply { show() }
}

fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun ProgressBar.show(){
    visibility=View.VISIBLE
}
fun ProgressBar.hide(){
    visibility=View.INVISIBLE
}
fun View.snackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }.show()
}


fun printDifference(startDate: Date, endDate: Date): String? {
    var different: Long = endDate.getTime() - startDate.getTime()
    println("startDate : $startDate")
    println("endDate : $endDate")
    println("different : $different")
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = different / daysInMilli
    different = different % daysInMilli
    val elapsedHours = different / hoursInMilli
    different = different % hoursInMilli
    val elapsedMinutes = different / minutesInMilli
    different = different % minutesInMilli
    val elapsedSeconds = different / secondsInMilli
    System.out.printf(
        "%d days, %d hours, %d minutes, %d seconds%n",
        elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds
    )
    return if (elapsedDays > 0) {
        "" + elapsedDays + "\tday"
    } else {
        if (elapsedHours > 0) {
            "" + elapsedHours + "\th"
        } else {
            if (elapsedMinutes > 0) {
                "" + elapsedMinutes + "\tm"
            } else "" + elapsedSeconds + "\ts"
        }
    }
}

fun GetAppVersion(context: Context): String {
    var version = ""
    try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        version = pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return version
}
fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}