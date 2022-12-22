package com.wiesoftware.spine.ui.home.menus.profile.editprofile

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository

/**
 * Created by Vivek kumar on 11/25/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class EditProfileViewmodel(val homeRepositry: HomeRepository) : ViewModel() {

    var normalname: String = ""
    var proffessionalname: String = ""
    var companyname: String = ""

    var normaldisplay_name: String = ""
    var proffessionaldisplay_name: String = ""
    var companydisplay_name: String = ""

    var normalbio: String = ""
    var proffessionalbio: String = ""
    var companybio: String = ""

    var interested: String = ""

    var normalcategory_name: String = ""
    var proffessionalcategory_name: String = ""
    var companycategory_name: String = ""

    var proffessionallanguages: String = ""
    var companylanguages: String = ""

    var proffessionaloffer_desciption: String = ""
    var companyoffer_desciption: String = ""

    var proffessionalkey_perfomance: String = ""
    var companykey_perfomance: String = ""


    var proffessionaldesease_pattern: String = ""
    var companydesease_pattern: String = ""

    var proffessionalqualification: String = ""
    var companyqualification: String = ""

    var proffessionalcompany_name: String = ""
    var comcompany_name: String = ""

    var proffessionalstreet_1: String = ""
    var companystreet_1: String = ""

    var proffessionalstreet_2: String = ""
    var companystreet_2: String = ""


    var proffessionalstreet_3: String = ""
    var companystreet_3: String = ""

    var proffessionalcity: String = ""
    var companycity: String = ""

    var proffessionalpostcode: String = ""
    var companypostcode: String = ""

    var proffessionalcountry: String = ""
    var companycountry: String = ""


    var proffessionalmetaverse_address: String = ""
    var companymetaverse_address: String = ""

    var proffessionaladdress: String = ""
    var companyaddress: String = ""

    var proffessionalwebsite: String = ""
    var companywebsite: String = ""

    var proffessionalemail: String = ""
    var companyemail: String = ""

    var proffessionalbusiness_mobile: String = ""
    var companybusiness_mobile: String = ""

    var proffessionalbusiness_phone: String = ""
    var companybusiness_phone: String = ""

    var accountType: String = "0"
    var proffessionalbusiness_mobile_code: String = ""
    var companybusiness_mobile_code: String = ""
    var proffessionalbusiness_phone_code: String = ""
    var companybusiness_phone_code: String = ""

    var listingType: String = "0"

    var editProfileEventListener: EditProfileEventListener? = null

    fun getLoggedInUser() = homeRepositry.getUser()

    fun onBack(view: View) {
        editProfileEventListener?.onBack()
    }

    fun addProfilePic(view: View) {
        editProfileEventListener?.addProfilePic()
    }

    fun addbgProfilePic(view: View){
        editProfileEventListener?.bgProfilePic()
    }

    fun onSave(view: View) {
        Log.e("accountType", "=" + accountType)


        if (accountType.equals("0")) {
            if (normalname.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (normaldisplay_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Display Name", Toast.LENGTH_SHORT).show()
            } else if (normalbio.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Short Bio", Toast.LENGTH_SHORT).show()
            } else if (interested.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter intersted in", Toast.LENGTH_SHORT).show()

            } else if (normalcategory_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please select category", Toast.LENGTH_SHORT).show()
            } else {
                editProfileEventListener?.onSaveProfile(
                    accountType,
                    normalname,
                    normaldisplay_name,
                    normalbio,
                    interested,
                    normalcategory_name
                )
            }
        }

        if (accountType.equals("1") && listingType.equals("1")) {
            if (proffessionalname.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (proffessionaldisplay_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Display Name", Toast.LENGTH_SHORT).show()
            } else if (proffessionalbio.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Short Bio", Toast.LENGTH_SHORT).show()
            } else if (proffessionaloffer_desciption.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter offer descriptions", Toast.LENGTH_SHORT).show()
            } else if (proffessionalkey_perfomance.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter key performance area",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else if (proffessionaldesease_pattern.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter desease pattern", Toast.LENGTH_SHORT)
                    .show()

            } else if (proffessionalcategory_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please select category ", Toast.LENGTH_SHORT).show()
            } else if (proffessionallanguages.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please select language ", Toast.LENGTH_SHORT).show()
            } else if (proffessionalqualification.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter qualifications", Toast.LENGTH_SHORT)
                    .show()

            } else if (proffessionalcompany_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter company name", Toast.LENGTH_SHORT).show()

            } else if (proffessionalstreet_1.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter street", Toast.LENGTH_SHORT).show()
            } else if (proffessionalcity.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter city", Toast.LENGTH_SHORT).show()
            } else if (proffessionalpostcode.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter postcode", Toast.LENGTH_SHORT).show()
            } else if (proffessionalcountry.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter country", Toast.LENGTH_SHORT).show()
            } /*else if (address.isNullOrEmpty()) {
            Toast.makeText(view.context, "Please enter address", Toast.LENGTH_SHORT).show()
        }*/ else if (proffessionalmetaverse_address.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter metaverse address", Toast.LENGTH_SHORT)
                    .show()
            } else if (proffessionalwebsite.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter website", Toast.LENGTH_SHORT).show()
            } else if (proffessionalemail.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter email", Toast.LENGTH_SHORT).show()
            } else if (proffessionalbusiness_phone.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter business phone number",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (proffessionalbusiness_mobile.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter business mobile number",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                editProfileEventListener?.onSaveProfile(
                    accountType,
                    proffessionalname,
                    proffessionaldisplay_name,
                    proffessionalbio,
                    interested,
                    proffessionalcategory_name
                )
            }
        }



        if (accountType.equals("1") && listingType.equals("2")) {
            if (companyname.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (companydisplay_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Display Name", Toast.LENGTH_SHORT).show()
            } else if (companybio.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter Short Bio", Toast.LENGTH_SHORT).show()
            } else if (companyoffer_desciption.isNullOrEmpty()) {
                Toast.makeText(view.context, "Enter offer descriptions", Toast.LENGTH_SHORT).show()
            } else if (companykey_perfomance.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter key performance area",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else if (companydesease_pattern.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter desease pattern", Toast.LENGTH_SHORT)
                    .show()

            } else if (companycategory_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please select category ", Toast.LENGTH_SHORT).show()
            } else if (companylanguages.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please select language ", Toast.LENGTH_SHORT).show()
            } else if (companyqualification.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter qualifications", Toast.LENGTH_SHORT)
                    .show()

            } else if (comcompany_name.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter company name", Toast.LENGTH_SHORT).show()

            } else if (companystreet_1.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter street", Toast.LENGTH_SHORT).show()
            } else if (companycity.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter city", Toast.LENGTH_SHORT).show()
            } else if (companypostcode.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter postcode", Toast.LENGTH_SHORT).show()
            } else if (companycountry.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter country", Toast.LENGTH_SHORT).show()
            } /*else if (address.isNullOrEmpty()) {
            Toast.makeText(view.context, "Please enter address", Toast.LENGTH_SHORT).show()
        }*/ else if (companymetaverse_address.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter metaverse address", Toast.LENGTH_SHORT)
                    .show()
            } else if (companywebsite.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter website", Toast.LENGTH_SHORT).show()
            } else if (companyemail.isNullOrEmpty()) {
                Toast.makeText(view.context, "Please enter email", Toast.LENGTH_SHORT).show()
            } else if (companybusiness_phone.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter business phone number",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (companybusiness_mobile.isNullOrEmpty()) {
                Toast.makeText(
                    view.context,
                    "Please enter business mobile number",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                editProfileEventListener?.onSaveProfile(
                    accountType,
                    companyname,
                    companydisplay_name,
                    companybio,
                    interested,
                    companycategory_name
                )
            }
        }
    }

    fun onSwitchAccount(view: View) {
        editProfileEventListener?.switchAccount()
    }

    fun onCategorySelect(view: View) {
        editProfileEventListener?.onCategorySelect()
    }

    fun onLanguageSelect(view: View) {
        editProfileEventListener?.onLanguageSelect()
    }


}