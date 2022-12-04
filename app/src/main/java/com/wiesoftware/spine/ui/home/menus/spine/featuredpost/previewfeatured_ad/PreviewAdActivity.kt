package com.wiesoftware.spine.ui.home.menus.spine.featuredpost.previewfeatured_ad

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paypal.android.sdk.payments.*
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPreviewAdBinding
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.EventAdData
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity.Companion.AD_TYPE
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity.Companion.EVENT_AD_DATA
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity.Companion.PIC_VID_AD_DATA
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.FeaturedPostActivity.Companion.POD_AD_DATA
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.PicVidAdData
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.PodAdData
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured.ThanksFeaturedActivity
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.bottomsheet_payment_option.view.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class PreviewAdActivity : AppCompatActivity(), KodeinAware, PreviewAdEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityPreviewAdBinding
    var adType = 0

    var link = ""
    var amount = "0";
    var currency = "USD";
    var AD_TYPES = ""
    var paymentDetails = "";
    var payBy = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_ad)
        val viewmodel = ViewModelProvider(this).get(PreviewAdViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.previewAdEventListener = this
        adType = intent.getIntExtra(AD_TYPE, 0)
        when (adType) {
            1 -> {
                setPicVidAdData(); AD_TYPES = getString(R.string.picture_or_video)
            }
            2 -> {
                setEventAdData(); AD_TYPES = getString(R.string.event)
            }
            3 -> {
                setPodAdData(); AD_TYPES = getString(R.string.podcast)
            }
            else -> {
                "Preview not available.".toast(this);onBackPressed()
            }
        }

    }

    private fun setPicVidAdData() {
        binding.picVidPreview.visibility = View.VISIBLE
        binding.eventAd.visibility = View.GONE
        binding.podAd.visibility = View.GONE

        val picVidAdData = intent.getSerializableExtra(PIC_VID_AD_DATA) as PicVidAdData
        val userId = picVidAdData.uid
        getUserDetails(userId)
        amount = picVidAdData.amount
        currency = if (picVidAdData.curency.equals("€")) {
            "EUR"
        } else {
            "USD"
        }
        val currentPhotoPath = picVidAdData.currentPhotoPath
        link = picVidAdData.picVidWebLink
        val picVidAdditionalLine = picVidAdData.picVidAdditionalLine
        try {
            val mImageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.imageView47.setImageBitmap(mImageBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.textView315.text = picVidAdditionalLine
    }

    private fun getUserDetails(userId: String) {
        lifecycleScope.launch {
            try {
                val uRes = homeRepositry.getUserDetails()
                if (uRes.status) {
                    val username = uRes.data.display_name ?: uRes.data.name
                    val img = uRes.image + uRes.data.user_image
                    when (adType) {
                        1 -> {
                            binding.textView312.text = username
                            Glide.with(binding.imageView45).load(img).error(R.drawable.ic_profile)
                                .into(binding.imageView45)
                        }
                        2 -> {
                            binding.textVieweve312.text = username
                            Glide.with(binding.imageVieweve45).load(img)
                                .error(R.drawable.ic_profile).into(binding.imageVieweve45)
                        }
                        3 -> {
                            binding.textViewpod312.text = username
                            Glide.with(binding.imageViewpod45).load(img)
                                .error(R.drawable.ic_profile).into(binding.imageViewpod45)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun setPodAdData() {
        binding.picVidPreview.visibility = View.GONE
        binding.eventAd.visibility = View.GONE
        binding.podAd.visibility = View.VISIBLE

        val picVidAdData = intent.getSerializableExtra(POD_AD_DATA) as PodAdData
        val userId = picVidAdData.uid
        getUserDetails(userId)
        amount = picVidAdData.amount
        currency = if (picVidAdData.curency.equals("€")) {
            "EUR"
        } else {
            "USD"
        }
        val currentPhotoPath = picVidAdData.currentPhotoPath
        link = picVidAdData.podWebLink
        val picVidAdditionalLine = picVidAdData.podAdditionalLine
        try {
            val mImageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.imageViewpod47.setImageBitmap(mImageBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.textViewpod315.text = picVidAdditionalLine
    }

    private fun setEventAdData() {
        binding.picVidPreview.visibility = View.GONE
        binding.eventAd.visibility = View.VISIBLE
        binding.podAd.visibility = View.GONE

        val picVidAdData = intent.getSerializableExtra(EVENT_AD_DATA) as EventAdData
        val userId = picVidAdData.uid
        getUserDetails(userId)
        amount = picVidAdData.amount
        currency = if (picVidAdData.curency.equals("€")) {
            "EUR"
        } else {
            "USD"
        }
        val currentPhotoPath = picVidAdData.currentPhotoPath
        link = picVidAdData.eventWebLink
        val picVidAdditionalLine = picVidAdData.eventAdditionalLine
        val eveTitle = picVidAdData.eventTitle
        binding.textVieweve313.text = eveTitle
        val eve_date = picVidAdData.eventStartDate
        val location = picVidAdData.location
        binding.textVieweve314.text = location

        val simpleDateFormat = SimpleDateFormat("yyyy-M-dd", Locale.getDefault())
        try {
            val date1: Date = simpleDateFormat.parse(eve_date)
            val dd = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val ss: String = dd.format(date1)
            Log.e("fmtDate: ", ss)
            val newDay = (ss.split(",")[1]).split(" ")[1]
            val newMonth = (ss.split(",")[1]).split(" ")[2]
            binding.textView76.text = newDay + " " + newMonth
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("fmtDate: ", e.message.toString())
        }

        try {
            val mImageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.imageVieweve47.setImageBitmap(mImageBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.textVieweve315.text = picVidAdditionalLine
    }

    private fun saveVidAdData() {
        val picVidAdData = intent.getSerializableExtra(PIC_VID_AD_DATA) as PicVidAdData
        val currentPhotoPath = picVidAdData.currentPhotoPath
        val photoURI = picVidAdData.photoURI
        val uri = Uri.parse(photoURI)
        val userId = picVidAdData.uid
        val ftype = picVidAdData.ftype
        val picVidWebLink = picVidAdData.picVidWebLink
        val picVidAdditionalLine = picVidAdData.picVidAdditionalLine
        val startDateSlot = picVidAdData.startDateSlot
        val startTimeSlot = picVidAdData.startTimeSlot
        val adType = picVidAdData.adType
        val adDuration = picVidAdData.durationId
        val picLat = picVidAdData.latitude
        val picLong = picVidAdData.longitude


        val file: File = File(currentPhotoPath)
        val requestFile: RequestBody = RequestBody.create(
            contentResolver.getType(
                uri
            )?.let {
                it.toMediaTypeOrNull()
            }, file
        )
        val fileAd: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        val fileType: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            ftype
        )
        val website: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidWebLink
        )
        val additionalLine: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidAdditionalLine
        )
        val slotDate: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startDateSlot
        )
        val slotTime: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startTimeSlot
        )
        val adT: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), adType)
        val durationAd: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            adDuration
        )
        val paymentdetails: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            paymentDetails
        )
        val payby: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            payBy
        )
        val picLatitude: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picLat
        )
        val picLongitude: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picLong
        )
        binding.button112.visibility = View.INVISIBLE
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addFeaturedAds(
                    fileAd,
                    uid,
                    durationAd,
                    slotDate,
                    slotTime,
                    adT,
                    fileType,
                    website,
                    additionalLine,
                    paymentdetails,
                    payby,
                    picLatitude,
                    picLongitude
                )
                Log.e("FeaturedAd:", "" + res)
                if (res.status) {
                    "${res.message}".toast(this@PreviewAdActivity)
                    startActivity(
                        Intent(
                            this@PreviewAdActivity,
                            ThanksFeaturedActivity::class.java
                        )
                    )
                }
                binding.button112.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                binding.button112.visibility = View.VISIBLE
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onNext() {
        startActivity(
            Intent(
                this@PreviewAdActivity,
                com.wiesoftware.spine.ui.home.menus.spine.featuredpost.PaymentActivity::class.java
            )
        )

//        paymentOption()
    }

    override fun onLinkClicked() {
        openChromeTab(link)
    }

    private fun openChromeTab(url: String) {
        val newUrl = if (url.startsWith("http://", true) || url.startsWith("https://", true)) {
            url
        } else {
            "http://$url"
        }
        val uri: Uri = Uri.parse(newUrl)
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .build()
        builder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        builder.setStartAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            this,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setShowTitle(true)
        if (isChromeInstalled()) {
            builder.build().intent.setPackage("com.android.chrome")
        }
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(this, uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isChromeInstalled(): Boolean {
        return try {
            getPackageManager().getPackageInfo("com.android.chrome", 0)
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun paymentOption() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_payment_option, null)
        val dialog: BottomSheetDialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.setOnShowListener {
            val dialogTmp: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout =
                dialogTmp.findViewById(R.id.design_bottom_sheet) as FrameLayout?
                    ?: return@setOnShowListener
            bottomSheet.background = null
        }
        dialog.window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(true)
        }
        view.imageButton84.setOnClickListener {
            dialog.dismiss()
        }
        view.button121.setOnClickListener {
            payAndPublish()
            dialog.dismiss()
        }
        view.radioGroup2.setOnCheckedChangeListener { radioGroup, i ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButton12 -> payWithPaypal()
                R.id.radioButton11 -> payWithCredit()
                R.id.radioButton10 -> payWithBank()
            }
        }
        dialog.show()
    }

    var paymentMethod = 0
    private fun payWithBank() {
        paymentMethod = 3
    }

    private fun payWithCredit() {
        paymentMethod = 2
    }

    private fun payWithPaypal() {
        paymentMethod = 1
    }

    private fun payAndPublish() {
        if (paymentMethod == 1) {
            //startActivity(Intent(this,PaypalPaymentActivity::class.java))
            val intent = Intent(this, PayPalService::class.java)
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
            startService(intent)

            startPayment()
        }
    }

    private fun startPayment() {
        val thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT)

    }

    private fun getThingToBuy(paymentIntentSale: String): PayPalPayment {

        return PayPalPayment(
            BigDecimal(amount), currency, AD_TYPES,
            paymentIntentSale
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {

                val confirm =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4))
                        Log.e(TAG, confirm.payment.toJSONObject().toString(4))
                        paymentDetails = confirm.toJSONObject().toString(4)
                        payBy = "Paypal"
                        when (adType) {
                            1 -> {
                                saveVidAdData()
                            }
                            2 -> {
                                saveEventData()
                            }
                            3 -> {
                                savePodData()
                            }
                            else -> {
                                "Invalid ad.".toast(this)
                            }
                        }
                    } catch (e: JSONException) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e)
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(
                    TAG,
                    "An invalid Payment or PayPalConfiguration was submitted. Please see the docs."
                )
            }
        }
    }

    private fun saveEventData() {
        val picVidAdData =
            intent.getSerializableExtra(FeaturedPostActivity.EVENT_AD_DATA) as EventAdData
        val currentPhotoPath = picVidAdData.currentPhotoPath
        val photoURI = picVidAdData.photoURI
        val uri = Uri.parse(photoURI)
        val userId = picVidAdData.uid
        val ftype = picVidAdData.ftype
        val picVidWebLink = picVidAdData.eventWebLink
        val picVidAdditionalLine = picVidAdData.eventAdditionalLine
        val startDateSlot = picVidAdData.startDateSlot
        val startTimeSlot = picVidAdData.startTimeSlot
        val adType = picVidAdData.adType
        val adDuration = picVidAdData.durationId

        val eveTitle = picVidAdData.eventTitle
        val eveType = picVidAdData.eventType
        val eveStartDate = picVidAdData.eventStartDate
        val eveEndDate = picVidAdData.eventEndDate
        val eveStartTime = picVidAdData.eventStartTime
        val eveEndTime = picVidAdData.eventEndTime
        val eveTimeZone = picVidAdData.timezone
        val eveLocation = picVidAdData.location
        val eveLatitude = picVidAdData.latitude
        val eveLongitude = picVidAdData.longitude

        val file: File = File(currentPhotoPath)
        val requestFile: RequestBody = RequestBody.create(
            contentResolver.getType(
                uri
            )?.let {
                it
                    .toMediaTypeOrNull()
            }, file
        )
        val fileAd: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        val fileType: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            ftype
        )
        val website: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidWebLink
        )
        val additionalLine: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidAdditionalLine
        )
        val slotDate: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startDateSlot
        )
        val slotTime: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startTimeSlot
        )
        val adT: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), adType)
        val durationAd: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            adDuration
        )
        val paymentdetails: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            paymentDetails
        )
        val payby: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            payBy
        )

        val eTitle: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveTitle
        )
        val eType: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveType
        )
        val eStartDate: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveStartDate
        )
        val eStartTime: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveStartTime
        )
        val eEndTime: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveEndTime
        )
        val eEndDate: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveEndDate
        )
        val eTimeZone: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveTimeZone
        )
        val eLocation: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveLocation
        )
        val eLatitude: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveLatitude
        )
        val eLongitude: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            eveLongitude
        )


        binding.button112.visibility = View.INVISIBLE
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addEventFeaturedAds(
                    fileAd,
                    uid,
                    durationAd,
                    slotDate,
                    slotTime,
                    adT,
                    fileType,
                    website,
                    additionalLine,
                    paymentdetails,
                    payby,
                    eTitle,
                    eType,
                    eStartDate,
                    eStartTime,
                    eEndTime,
                    eEndDate,
                    eTimeZone,
                    eLocation,
                    eLatitude,
                    eLongitude
                )
                Log.e("FeaturedAd:", "" + res)
                if (res.status) {
                    "${res.message}".toast(this@PreviewAdActivity)
                    startActivity(
                        Intent(
                            this@PreviewAdActivity,
                            ThanksFeaturedActivity::class.java
                        )
                    )
                }
                binding.button112.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                binding.button112.visibility = View.VISIBLE
            }
        }
    }

    private fun savePodData() {
        val picVidAdData =
            intent.getSerializableExtra(FeaturedPostActivity.POD_AD_DATA) as PodAdData
        val currentPhotoPath = picVidAdData.currentPhotoPath
        val photoURI = picVidAdData.photoURI
        val uri = Uri.parse(photoURI)
        val userId = picVidAdData.uid
        val ftype = picVidAdData.ftype
        val picVidWebLink = picVidAdData.podWebLink
        val picVidAdditionalLine = picVidAdData.podAdditionalLine
        val startDateSlot = picVidAdData.startDateSlot
        val startTimeSlot = picVidAdData.startTimeSlot
        val adType = picVidAdData.adType
        val adDuration = picVidAdData.durationId
        val podLat = picVidAdData.latitude
        val podLong = picVidAdData.longitude

        val file: File = File(currentPhotoPath)
        val requestFile: RequestBody = RequestBody.create(
            contentResolver.getType(
                uri
            )?.let {
                it
                    .toMediaTypeOrNull()
            }, file
        )
        val fileAd: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val uid: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        val fileType: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            ftype
        )
        val website: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidWebLink
        )
        val additionalLine: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            picVidAdditionalLine
        )
        val slotDate: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startDateSlot
        )
        val slotTime: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            startTimeSlot
        )
        val adT: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), adType)
        val durationAd: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            adDuration
        )
        val paymentdetails: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            paymentDetails
        )
        val payby: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            payBy
        )
        val plat: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            podLat
        )
        val plong: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            podLong
        )
        binding.button112.visibility = View.INVISIBLE
        lifecycleScope.launch {
            try {
                val res = homeRepositry.addFeaturedAds(
                    fileAd,
                    uid,
                    durationAd,
                    slotDate,
                    slotTime,
                    adT,
                    fileType,
                    website,
                    additionalLine,
                    paymentdetails,
                    payby,
                    plat,
                    plong
                )
                Log.e("FeaturedAd:", "" + res)
                if (res.status) {
                    "${res.message}".toast(this@PreviewAdActivity)
                    startActivity(
                        Intent(
                            this@PreviewAdActivity,
                            ThanksFeaturedActivity::class.java
                        )
                    )
                }
                binding.button112.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                binding.button112.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            stopService(Intent(this, PayPalService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = "paymentSpine"

        /**
         * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
         * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
         * from https://developer.paypal.com
         * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
         * without communicating to PayPal's servers.
         */
        private val CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK

        private val CONFIG_CLIENT_ID =
            "ASSRMYk_ODO4dgJcCJjAeTz7T78fYRMMbYMFIma7K0RshBZ3g2BCS8wlkaKdoAJo9ax9VSsBCuYmAOCa"

        private val REQUEST_CODE_PAYMENT = 1

        private val config = PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Spine")
            .merchantPrivacyPolicyUri(Uri.parse(Api.PRIVACY_SPINE))
            .merchantUserAgreementUri(Uri.parse(Api.TERMS_SPINE))
    }

}