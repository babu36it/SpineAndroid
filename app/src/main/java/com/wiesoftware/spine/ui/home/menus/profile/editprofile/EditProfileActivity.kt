package com.wiesoftware.spine.ui.home.menus.profile.editprofile

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.MultiLanguageAdapter
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.data.net.reponses.LangData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityEditProfileBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.MutilpeSpineCatAdapter
import com.wiesoftware.spine.ui.home.menus.events.addevents.SpinerCatAdapter
import com.wiesoftware.spine.ui.home.menus.spine.foryou.BASE_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.adp_lng_selections.*
import kotlinx.android.synthetic.main.bottomsheet_picker.view.*
import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*


class EditProfileActivity : AppCompatActivity(), KodeinAware, EditProfileEventListener,
    AdapterView.OnItemSelectedListener, MutilpeSpineCatAdapter.OnEveItemChecked,
    MutilpeSpineCatAdapter.ListValue, MultiLanguageAdapter.OnLanguageItemChecked,
    MultiLanguageAdapter.ListValue {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    val REQUEST_TAKE_PHOTO = 1
    val GALLERY_REQ = 2
    var currentPhotoPath: String? = null
    lateinit var photoURI: Uri
    val PERMISSION_REQUEST_CODE = 94

    private val TAG = "PermissionDemo"
    var imageUriSign: Uri? = null
    var mFilePathSign = ""


    var picname: String? = null
    val RECORD_REQUEST_CODE = 101


    override val kodein by kodein()
    val factory: EditProfileViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityEditProfileBinding
    lateinit var editProfileViewModel: EditProfileViewmodel
    lateinit var user_id: String

    var category: String = ""
    var categoryIds: String = "0"
    var Langauge: String = ""
    var LanguageIds: String = "0"
    var website: String = "."
    var contactEmail: String = "."
    var businessPhone: String = "."
    var buisinessLocation: String = "1"
    var businessAddress: String = "."
    lateinit var progressDialog: ProgressDialog
    var lanngData: List<LangData> = ArrayList()

    var catData: List<EventCatData> = ArrayList<EventCatData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        editProfileViewModel =
            ViewModelProvider(this, factory).get(EditProfileViewmodel::class.java)
        binding.viewmodel = editProfileViewModel
        editProfileViewModel.editProfileEventListener = this
        progressDialog = ProgressDialog(this)
        editProfileViewModel.getLoggedInUser().observe(this, Observer { user ->
            user_id = user.users_id!!
            val u_name: String = user.name!!
            binding.edtName.setText(u_name)
            setUserDetails()
        })
        binding.bLoc.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                buisinessLocation = "1"
                binding.textView14.visibility = View.VISIBLE
                binding.ll6.visibility = View.VISIBLE
            } else {
                buisinessLocation = "0"
                binding.textView14.visibility = View.GONE
                binding.ll6.visibility = View.GONE
            }
        }

        binding.tvPracticionarListing.setOnClickListener {
            binding.tvPracticionarListing.setBackgroundResource(R.drawable.dark_button)
            binding.tvCompanyListing.setBackgroundDrawable(null)
//            binding.tvCompanyListing.setBackgroundResource(null)
            binding.constraintLayout5.visibility = View.VISIBLE
            binding.llCompanyListing.visibility = View.GONE
            editProfileViewModel.listingType = "1"
        }

        binding.tvCompanyListing.setOnClickListener {
            binding.tvCompanyListing.setBackgroundResource(R.drawable.dark_button)
            binding.tvPracticionarListing.setBackgroundDrawable(null)
            binding.constraintLayout5.visibility = View.GONE
            binding.llCompanyListing.visibility = View.VISIBLE
            editProfileViewModel.listingType = "2"
        }

        binding.edtAboutUs.movementMethod = ScrollingMovementMethod()
        binding.edtAboutUs.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        binding.edtAboutUs.isSingleLine = false
        binding.edtAboutUs.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION

        val mNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvNameCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtName.addTextChangedListener(mNameTextWatcher)

        val mDisplayNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvDisplayNameCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtDisplayName.addTextChangedListener(mDisplayNameTextWatcher)


        val mAboutmeTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvAboutCounter.text = (140 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtAboutUs.addTextChangedListener(mAboutmeTextWatcher)


        val mInterstedInTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvInterstedCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtIntersted.addTextChangedListener(mInterstedInTextWatcher)


        val mCategoriesTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvAutomaticSuggetions.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtAutomaticSuggetions.addTextChangedListener(mCategoriesTextWatcher)

        val mProffessionalNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvProffesionalCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtProfessionalName.addTextChangedListener(mProffessionalNameTextWatcher)


        val mProffessionalDisplayNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvProffesionalDisplayNameCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtProffesionalDisplayName.addTextChangedListener(
            mProffessionalDisplayNameTextWatcher
        )


        val mCompanyNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvcomapnyCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtCName.addTextChangedListener(mCompanyNameTextWatcher)


        val mCompanyDisplayNameTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.tvCompanyDisplayNameCounter.text = (40 - s.length).toString()
                //This sets a textview to the current length
//                binding.tvNameCounter.setText(40-s.length);
            }

            override fun afterTextChanged(s: Editable) {
            }
        }

        binding.edtCompanyDisplayName.addTextChangedListener(mCompanyDisplayNameTextWatcher)

        getEventCategories("")

        getLanguages()
    }

    private fun setUserDetails() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getUserDetails()
                if (res.status) {
                    dismissProgressDailog()
                    BASE_IMAGE = res.image
                    val data = res.data
                    val name = data.name
                    val display_name = data.display_name
                    val bio = data.bio
                    val pic = data.user_image
                    binding.edtName.setText(name)
                    binding.edtDisplayName.setText(display_name)
                    binding.edtAboutUs.setText(bio)
                    binding.edtIntersted.setText(data.interested)
                    binding.TvEvCat.text = data.categoryName

                    //practicionar Listing
                    binding.edtProfessionalName.setText(data.name)
                    binding.edtProffesionalDisplayName.setText(data.display_name)
                    binding.edtProffessionalAboutUs.setText(data.bio)
                    binding.edtOfferDescriptions.setText(data.offer_desciption)
                    binding.edtKeyPerformance.setText(data.key_perfomance)
                    binding.edtDeseasePattern.setText(data.desease_pattern)
                    binding.edtQualifications.setText(data.qualification)
                    binding.edtProffessionalCompanyName.setText(data.company_name)
                    binding.edtProffessioanlStreet1.setText(data.street_1)
                    binding.edtProffessionalStreet2.setText(data.street_2)
                    binding.edtProffessioanlStreet3.setText(data.street_3)
                    binding.edtProffessionalCity.setText(data.city)
                    binding.edtProffessioanlPostCode.setText(data.postcode)
                    binding.edtProffeCountry.setText(data.country)
                    binding.tvProffessionalCat.text = data.categoryName
                    binding.tvProffessionalLanguages.setText(data.languages)
                    binding.edtProffessionalAddress.setText(data.address.toString())
                    binding.edtMetavarseAddress.setText(data.metaverse_address)
                    binding.edtProffessionalWebsite.setText(data.website)
                    binding.edtProffessionalEmail.setText(data.email)
                    binding.edtProffeBusinessPhoneNumber.setText(data.business_phone)
                    binding.edtProffessionalMobileNumber.setText(data.business_mobile)
                    binding.edtBusinessIsoCode.setText(data.business_phone_code)
                    binding.edtProffMobileNumberIso.setText(data.business_mobile_code)

                    //company listing
                    binding.edtCName.setText(data.name)
                    binding.edtCompanyDisplayName.setText(data.display_name)
                    binding.edtCompanyAboutUs.setText(data.bio)
                    binding.edtCompanyOffer.setText(data.offer_desciption)
                    binding.edtCompanyKey.setText(data.key_perfomance)
                    binding.edtCompanyPattern.setText(data.desease_pattern)
                    binding.edtCompanyQualifications.setText(data.qualification)
                    binding.edtCompanyName.setText(data.company_name)
                    binding.edtCompanyStreet1.setText(data.street_1)
                    binding.edtCompanyStreet2.setText(data.street_2)
                    binding.edtCompanyStreet3.setText(data.street_3)
                    binding.edtCompanyCity.setText(data.city)
                    binding.edtCompanyPostCode.setText(data.postcode)
                    binding.edtCompanyCountry.setText(data.country)
                    binding.edtCompanyAddress.setText(data.address)
                    binding.edtCompanyMetavarseAddress.setText(data.metaverse_address)
                    binding.edtCompanyWebsite.setText(data.website)
                    binding.edtCompanyEmail.setText(data.email)
                    binding.tvCompanyCat.text = data.categoryName
                    binding.tvCompanyLanguage.setText(data.languages)
                    binding.edtCompanyBusinessMobile.setText(data.business_mobile)
                    binding.edtCompanyBusinessPhone.setText(data.business_phone)
                    binding.edtBusinessPhoneIso.setText(data.business_phone_code)
                    binding.edtCompanyMobileIso.setText(data.business_mobile_code)

                    categoryIds = data.category

                    Glide.with(this@EditProfileActivity)
                        .load("https://thespiritualnetwork.com/assets/upload/profile/" + pic)
                        .placeholder(R.drawable.userprofile)
                        .into(binding.cvProfile)

                    Glide.with(this@EditProfileActivity)
                        .load("https://thespiritualnetwork.com/assets/upload/profile/" + data.bg_image)
                        .placeholder(R.drawable.bgimage)
                        .into(binding.bgProfile)

                    if (data.account_mode.equals("0")) {
                        llProfile.visibility = View.VISIBLE
                        constraintLayout5.visibility = View.GONE
                        llCompanyListing.visibility = View.GONE
                        binding.llTabbar.visibility = View.GONE
                        editProfileViewModel.accountType = "0"

                        binding.tvSwitch.text = getString(R.string.switch_to_professional_account)

                        binding.switchButton.isChecked = false
                    } else {
                        llProfile.visibility = View.GONE
                        constraintLayout5.visibility = View.VISIBLE
                        //llCompanyListing.visibility = View.VISIBLE
                        binding.llTabbar.visibility = View.VISIBLE
                        editProfileViewModel.accountType = "1"
                        editProfileViewModel.listingType = "1"
                        binding.tvSwitch.text = getString(R.string.back_to_normal_account)

                        binding.switchButton.isChecked = true
                    }

                } else {
                    Toast.makeText(this@EditProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    override fun onBack() {
        onBackPressed()
    }

    override fun onSaveProfile(
        accountType1: String,
        user_name: String,
        display_name: String,
        short_bio: String,
        intersted: String,
        category: String
    ) {

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.editProfille(
                    editProfileViewModel.accountType,
                    if (editProfileViewModel.accountType.equals("1")) editProfileViewModel.listingType else "",

                    if (editProfileViewModel.accountType.equals("0")) binding.edtName.text.toString()
                        .trim() else if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProfessionalName.text.toString()
                        .trim()
                    else binding.edtCName.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("0")) binding.edtDisplayName.text.toString()
                        .trim()
                    else if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffesionalDisplayName.text.toString()
                        .trim()
                    else binding.edtCompanyDisplayName.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("0")) binding.edtAboutUs.text.toString()
                        .trim()
                    else if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalAboutUs.text.toString()
                        .trim()
                    else binding.edtCompanyAboutUs.text.toString().trim(),

                    categoryIds,

                    binding.edtIntersted.text.toString().trim(),
                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtOfferDescriptions.text.toString()
                        .trim()
                    else binding.edtCompanyOffer.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtKeyPerformance.text.toString()
                        .trim()
                    else binding.edtCompanyKey.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtDeseasePattern.text.toString()
                        .trim()
                    else binding.edtCompanyPattern.text.toString().trim(),

                    Langauge,
                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtQualifications.text.toString()
                        .trim()
                    else binding.edtCompanyQualifications.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalCompanyName.text.toString()
                        .trim()
                    else binding.edtCompanyName.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessioanlStreet1.text.toString()
                        .trim()
                    else binding.edtCompanyStreet1.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalStreet2.text.toString()
                        .trim()
                    else binding.edtCompanyStreet2.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessioanlStreet3.text.toString()
                        .trim()
                    else binding.edtCompanyStreet3.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalCity.text.toString()
                        .trim()
                    else binding.edtCompanyCity.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessioanlPostCode.text.toString()
                        .trim()
                    else binding.edtCompanyPostCode.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffeCountry.text.toString()
                        .trim()
                    else binding.edtCompanyCountry.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtMetavarseAddress.text.toString()
                        .trim()
                    else binding.edtCompanyMetavarseAddress.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtMetavarseAddress.text.toString()
                        .trim()
                    else binding.edtCompanyMetavarseAddress.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalWebsite.text.toString()
                        .trim()
                    else binding.edtCompanyWebsite.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalEmail.text.toString()
                        .trim()
                    else binding.edtCompanyEmail.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffeBusinessPhoneNumber.text.toString()
                        .trim()
                    else binding.edtCompanyBusinessPhone.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffessionalMobileNumber.text.toString()
                        .trim()
                    else binding.edtCompanyBusinessMobile.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtBusinessIsoCode.text.toString()
                        .trim()
                    else binding.edtBusinessPhoneIso.text.toString().trim(),

                    if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                            "1"
                        )
                    ) binding.edtProffMobileNumberIso.text.toString()
                        .trim()
                    else binding.edtCompanyMobileIso.text.toString().trim(),
                )
                if (res.status) {
                    dismissProgressDailog()
                    setUserDetails()
                    Toast.makeText(this@EditProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    dismissProgressDailog()
                    Toast.makeText(
                        this@EditProfileActivity,
                        res.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                display_name
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }

    override fun switchAccount() {
        if (editProfileViewModel.accountType.equals("0")) {
            binding.llTabbar.visibility = View.VISIBLE
            binding.constraintLayout5.visibility = View.VISIBLE
            binding.llCompanyListing.visibility = View.GONE
            binding.llProfile.visibility = View.GONE
            editProfileViewModel.accountType = "1"
            editProfileViewModel.listingType = "1"
            binding.tvSwitch.text = getString(R.string.back_to_normal_account)
        } else {
            binding.llTabbar.visibility = View.GONE
            binding.constraintLayout5.visibility = View.GONE
            binding.llCompanyListing.visibility = View.GONE
            binding.llProfile.visibility = View.VISIBLE
            editProfileViewModel.accountType = "0"
            editProfileViewModel.listingType = ""
            binding.tvSwitch.text = getString(R.string.switch_to_professional_account)
        }
    }

    override fun addProfilePic() {
        showPicker()
    }

    override fun bgProfilePic() {
        showBgPicker()
    }

    override fun onCategorySelect() {
        openEventDialog()
    }

    override fun onLanguageSelect() {
        openLanguageDialog()
    }


    private fun showPicker() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_picker, null)
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
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnFollow.setOnClickListener {
            picname = "profilepic"
            if (checkpermission()!!) {
                selectImage(this, 0)
            } else {
                setupPermissions()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            picname = "profilepic"
            if (checkpermission()!!) {
                selectImage(this, 1)
            } else {
                setupPermissions()
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showBgPicker() {
        val view: View = layoutInflater.inflate(R.layout.bottomsheet_picker, null)
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
            dialog.setCancelable(false)
        }
        view.btnCan.setOnClickListener {
            dialog.dismiss()
        }
        view.btnFollow.setOnClickListener {
            picname = "profileBgpic"
            if (checkpermission()!!) {
                selectImage(this, 0)
            } else {
                setupPermissions()
            }
            dialog.dismiss()
        }
        view.btnOnline.setOnClickListener {
            //startActivity(Intent(this, CustomCameraActivity::class.java))
            picname = "profileBgpic"
            if (checkpermission()!!) {
                selectImage(this, 1)
            } else {
                setupPermissions()
            }
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun updateProfilePic() {

        val multiPartRepeatString = "application/image"
        var facility_image: MultipartBody.Part? = null

        if (imageUriSign != null && imageUriSign!!.path != null) {
            val file = File(mFilePathSign)
            val signPicBody = file.asRequestBody(multiPartRepeatString.toMediaTypeOrNull())
            facility_image = MultipartBody.Part.createFormData("image", file.name, signPicBody)
            // val signPicBody = RequestBody.create(parse.parse(multiPartRepeatString), file)

            //facility_image = createFormData.createFormData("profile_image", file.name, signPicBody)
        }


        /* val file: File = File(currentPhotoPath!!)
         val requestFile: RequestBody = RequestBody.create(
             contentResolver.getType(photoURI)?.let { it.toMediaTypeOrNull() },
             file
         )
         val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
             "image",
             file.name,
             requestFile
         )*/

        val uid: RequestBody = user_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.updateUserProfilePic(facility_image)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@EditProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                } else {
                    dismissProgressDailog()
                    Log.e("error", res.message)
                    //    "Oops! Something went wrong".toast(this@EditProfileActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    private fun updateBgProfilePic() {

        val multiPartRepeatString = "application/image"
        var facility_image: MultipartBody.Part? = null

        if (imageUriSign != null && imageUriSign!!.path != null) {
            val file = File(mFilePathSign)
            val signPicBody = file.asRequestBody(multiPartRepeatString.toMediaTypeOrNull())
            facility_image = MultipartBody.Part.createFormData("image", file.name, signPicBody)
            // val signPicBody = RequestBody.create(parse.parse(multiPartRepeatString), file)

            //facility_image = createFormData.createFormData("profile_image", file.name, signPicBody)
        }


        /* val file: File = File(currentPhotoPath!!)
         val requestFile: RequestBody = RequestBody.create(
             contentResolver.getType(photoURI)?.let { it.toMediaTypeOrNull() },
             file
         )
         val img_file: MultipartBody.Part = MultipartBody.Part.createFormData(
             "image",
             file.name,
             requestFile
         )*/

        val uid: RequestBody = user_id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.updateUserBgProfilePic(facility_image!!)
                if (res.status) {
                    dismissProgressDailog()
                    Toast.makeText(this@EditProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                } else {
                    dismissProgressDailog()
                    Log.e("error", res.message)
                    //    "Oops! Something went wrong".toast(this@EditProfileActivity)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    private fun setEventCategories(catData: List<EventCatData>) {
        val list: MutableList<String> = ArrayList()
        list.add(getString(R.string.select))
        for (data in catData) {
            list.add(data.category_name)
        }
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.proCat) {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@EditProfileActivity
            prompt = getString(R.string.select)
            gravity = Gravity.CENTER
        }
    }

    fun openEventDialog() {
        category = ""
        categoryIds = ""
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.adp_cat_selection)
        dialog.rvcats.also { rv ->
            rv.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            catData.let {
                rv.adapter =
                    MutilpeSpineCatAdapter(it, this@EditProfileActivity, this@EditProfileActivity)
            }
        }
        val send = dialog.findViewById(R.id.button53) as TextView
        val textView131 = dialog.findViewById(R.id.textView131) as TextView
        val cancel = dialog.findViewById(R.id.button52) as Button
        val edt_search_category = dialog.findViewById<EditText>(R.id.edt_search_category)
        send.setOnClickListener {
            dialog.dismiss()
        }

        textView131.setOnClickListener {
            dialog.dismiss()
        }

        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value = s.toString()
                Log.e("valueee", value.toString())

                getEventCategories(value)

                dialog.rvcats.also { rv ->

                    rv.layoutManager =
                        LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                    rv.setHasFixedSize(true)
                    catData.let {
                        rv.adapter = MutilpeSpineCatAdapter(
                            it,
                            this@EditProfileActivity,
                            this@EditProfileActivity
                        )
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s = s.toString()
                Log.e("datatat", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        cancel.setOnClickListener {

            dialog.dismiss()
            getEventCategories(" ")
            dialog.rvcats.also { rv ->
                rv.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                rv.setHasFixedSize(true)
                catData.let {
                    rv.adapter = MutilpeSpineCatAdapter(it, this, this)
                }
            }
        }
        dialog.show()
    }


    fun openLanguageDialog() {
        Langauge = ""
        LanguageIds = ""
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.adp_lng_selections)
        dialog.rvLanguage.also { rv ->
            rv.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            lanngData.let {
                rv.adapter =
                    MultiLanguageAdapter(it, this@EditProfileActivity, this@EditProfileActivity)
            }
        }
        val send = dialog.findViewById(R.id.button53) as TextView
        val textView131 = dialog.findViewById(R.id.textView131) as TextView
        val cancel = dialog.findViewById(R.id.button52) as Button
        val edt_search_category = dialog.findViewById<EditText>(R.id.edt_search_category)
        send.setOnClickListener {
            dialog.dismiss()
        }

        textView131.setOnClickListener {
            dialog.dismiss()
        }

/*
        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value = s.toString()
                Log.e("valueee", value.toString())

                getEventCategories(value)

                dialog.rvcats.also { rv ->

                    rv.layoutManager =
                        LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                    rv.setHasFixedSize(true)
                    catData.let {
                        rv.adapter = MutilpeSpineCatAdapter(
                            it,
                            this@EditProfileActivity,
                            this@EditProfileActivity
                        )
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s = s.toString()
                Log.e("datatat", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
*/
        cancel.setOnClickListener {

            dialog.dismiss()
            getLanguages()
            dialog.rvLanguage.also { rv ->
                rv.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                rv.setHasFixedSize(true)
                lanngData.let {
                    rv.adapter = MultiLanguageAdapter(it, this, this)
                }
            }
        }
        dialog.show()
    }


    private fun getEventCategories(value: String) {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val catRes = homeRepositry.newgetEventCatRes(value)
                if (catRes.status) {
                    dismissProgressDailog()
                    catData = catRes.data
                    //setEventCategories(catData)
                } else {
                    Toast.makeText(this@EditProfileActivity, catRes.message, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()
            }
        }
    }


    private fun getLanguages() {
        lifecycleScope.launch {
            try {
                showProgressDialog()
                val res = homeRepositry.getPodcastLanguage()
                if (res.status) {
                    dismissProgressDailog()
                    lanngData = res.data
                    // setLanguages(lanngData)
                } else {
                    dismissProgressDailog()
                    Toast.makeText(this@EditProfileActivity, res.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                e.printStackTrace()
                dismissProgressDailog()

            } catch (e: NoInternetException) {
                e.printStackTrace()
                dismissProgressDailog()

            }
        }
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        p0?.selectedItem?.toast(this)
        category = p0?.selectedItem.toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {
        if (b) {
            if (category.isEmpty()) {
                category = eveCataData.category_name
                categoryIds = eveCataData.id
            } else {
                category = category + "," + eveCataData.category_name
                categoryIds = categoryIds + "," + eveCataData.id
            }
        } else {
            categoryIds = categoryIds.replace(eveCataData.id + ",", "")
            category = category.replace(eveCataData.category_name + ",", "")
        }
        if (editProfileViewModel.accountType.equals("0")) {
            binding.TvEvCat.text = category
        } else if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                "1"
            )
        ) {
            binding.tvProffessionalCat.text = category

        } else {
            binding.tvCompanyCat.setText(category)
        }

    }

    override fun onclick(Event: EventCatData) {
        if (editProfileViewModel.accountType.equals("0")) {
            binding.TvEvCat.text = Event.category_name
        } else if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                "1"
            )
        ) {
            binding.tvProffessionalCat.text = Event.category_name
        } else {
            binding.tvCompanyCat.setText(Event.category_name)
        }
        categoryIds = Event.id
        category = Event.category_name
        Log.e("list", Event.id)

    }


    //=============Image take================
    private fun selectImage(context: Context, value: Int) {

        if (value == 0) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 0)

        } else if (value == 1) {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, 1)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 ->
                    if (data != null) {

                        var datacamera = data.extras!!.get("data") as Bitmap

                        imageUriSign = getImageUri(this, datacamera)
                        mFilePathSign = getAbsolutePath(imageUriSign)
                        Log.e("paths", mFilePathSign)
                        Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show()
                        if (picname == "profilepic") {
                            binding.cvProfile.setImageBitmap(datacamera)
                            val baos = ByteArrayOutputStream()
                            datacamera.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val images = baos.toByteArray()
                            // Log.e("imageses",images.toString())
                            //  profileimageString = Base64.encodeToString(images, Base64.DEFAULT)
                            updateProfilePic()
                        } else if (picname == "profileBgpic") {
                            binding.bgProfile.setImageBitmap(datacamera)
                            val baos = ByteArrayOutputStream()
                            datacamera.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val images = baos.toByteArray()
                            updateBgProfilePic()
                        }
                    }
                1 ->
                    try {
                        imageUriSign = data!!.data
                        val imageStream4: InputStream =
                            contentResolver.openInputStream(imageUriSign!!)!!
                        val selectedImage4 = BitmapFactory.decodeStream(imageStream4)
                        val selectedImageUri = data.data
                        val filePath = arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = contentResolver.query(
                            selectedImageUri!!,
                            filePath, null, null, null
                        )
                        cursor!!.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePath[0])
                        mFilePathSign = cursor.getString(columnIndex)
                        Log.e("paths", mFilePathSign)
                        cursor.close()
                        if (picname == "profilepic") {
                            binding.cvProfile.setImageBitmap(selectedImage4)
                            updateProfilePic()
                        } else if (picname == "profileBgpic") {
                            binding.bgProfile.setImageBitmap(selectedImage4)
                            updateBgProfilePic()
                        }

                        // imageView.tag = "profile";
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "IMG_" + Calendar.getInstance().time,
            null
        )
        return Uri.parse(path)
    }

    fun getAbsolutePath(uri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(uri!!, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }


    ///===============Check Permission============

    ///===============Check Permission============
    fun checkpermission(): Boolean? {
        val camerapermission =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
        return camerapermission == PackageManager.PERMISSION_GRANTED
    }

    fun setupPermissions() {
        val permissions =
            (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), RECORD_REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Log.i(TAG, "Permission has been granted by user")
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Log.i(TAG, "Permission has been denied by user")
                }
                return
            }
        }
    }

    private fun showProgressDialog() {
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissProgressDailog() {
        progressDialog.dismiss()
    }

    override fun onLanguageClick(lang: LangData) {
        if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                "1"
            )
        ) {
            binding.tvProffessionalLanguages.text = Langauge
        } else {
            binding.tvCompanyLanguage.text = Langauge

        }
        LanguageIds = lang.id
        Langauge = lang.name
        Log.e("list", lang.id)
    }

    override fun onLanguageItemChecked(langData: LangData, b: Boolean) {
        if (b) {
            if (Langauge.isEmpty()) {
                Langauge = langData.name
                LanguageIds = langData.id
            } else {
                Langauge = Langauge + "," + langData.name
                LanguageIds = LanguageIds + "," + langData.id
            }
        } else {
            LanguageIds = LanguageIds.replace(langData.id + ",", "")
            Langauge = Langauge.replace(langData.name + ",", "")
        }
        if (editProfileViewModel.accountType.equals("1") && editProfileViewModel.listingType.equals(
                "1"
            )
        ) {
            binding.tvProffessionalLanguages.text = Langauge
        } else {
            binding.tvCompanyLanguage.text = Langauge

        }
    }
}