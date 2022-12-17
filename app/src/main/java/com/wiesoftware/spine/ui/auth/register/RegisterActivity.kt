package com.wiesoftware.spine.ui.auth.register

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.data.repo.AuthRepository
import com.wiesoftware.spine.data.repo.EventRepository
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityRegisterBinding
import com.wiesoftware.spine.ui.auth.AuthViewModelFactory
import com.wiesoftware.spine.ui.auth.otp.OtpActivity
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.events.addevents.SpinerCatAdapter
import com.wiesoftware.spine.util.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.coroutines.launch
import org.json.JSONException
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class RegisterActivity : AppCompatActivity(), RegisterEventListener,KodeinAware,
    SpinerCatAdapter.OnEveItemChecked,SpinerCatAdapter.ListValue {

    lateinit var callbackManager: CallbackManager

    var category=""
    var categoryIds=""
    var catData: List<EventCatData> = ArrayList<EventCatData>()

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    val authRepositry: AuthRepository by instance()
    val homeRepositry: HomeRepository by instance()
    val eventRepository: EventRepository by instance()
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding= DataBindingUtil.setContentView(this,R.layout.activity_register)
        val viewModel=ViewModelProvider(this,factory).get(RegisterViewModel::class.java)
        binding.viewmodel=viewModel
        callbackManager=CallbackManager.Factory.create()
        viewModel.registerEventListener=this
        getEventCategories("")



        binding.tvCat.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        })


    }

    fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onRegistrationStart() {
        progressBar.show()
    }

    override fun onRegistrationSuccess(signupResponse: User?) {
        showSuccessDialog(signupResponse!!)

        progressBar.hide()
    }

    private fun showSuccessDialog(signupResponse: User) {
        val builder = AlertDialog.Builder(this)
        builder.create().setTitle(getString(R.string.spine_success_alert))
        builder.setMessage(getString(R.string.you_r_registered_successfully_alert))
        builder.setPositiveButton(android.R.string.ok){dialog,which->
            toast("${signupResponse.name} is Registered successfully.")
            toast("Your otp is: ${signupResponse.verification_pin}")
            val intent= Intent(this, OtpActivity::class.java)
            intent.putExtra(REGISTERED_USER,signupResponse)
            startActivity(intent)
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onRegistrationFailed(s: String) {
        progressBar.hide()
        root_layout.snackbar(s)
    }

    private fun getEventCategories(value:String) {

        lifecycleScope.launch {
            try {
                val catRes=eventRepository.getEventCatRes(value)
                if (catRes.status){
                    catData=catRes.data
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onCatSelection() {

        openEventDialog()
    }

    override fun onRegistration(userName: String, email: String, password: String, town: String) {
        progressBar.show()
        Coroutines.main {
            try {

                val response=authRepositry.userSignup(userName,email,password,town,categoryIds)

                progressBar.hide()
                if (response.status){
                    response.data?.let {
                        onRegistrationSuccess(it)
                        //authRepositry.saveUser(it)
                        return@main
                    }
                }

                else{

                    Utils.showToast(this,response.message)

                }
            }catch (e:ApiException){
                progressBar.hide()
                e.printStackTrace()
            }catch (e:NoInternetException){
                progressBar.hide()
                e.printStackTrace()
            }

        }
    }

    override fun onPrivacyPolicyClick() {
        openChromeTab(Api.PRIVACY_SPINE)
    }

    override fun onServiceClick() {
        openChromeTab(Api.TERMS_SPINE)
    }

    override fun onFacebookSignup() {

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            getFbData(accessToken)
        } else {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
            LoginManager.getInstance().registerCallback(callbackManager,object :
                FacebookCallback<LoginResult?> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.let {
                        getFbData(it)
                    }
                }

                override fun onCancel() {
                    toast("Facebook registration cancelled")
                }

                override fun onError(error: FacebookException?) {
                    toast(error?.message!!)
                }

            })
        }
    }

    private fun getFbData(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`, response ->
            try {
                Log.e("fbres:",""+response)
                Log.e("fbobj:",""+`object`)
                val name = `object`.getString("name")
                val id = `object`.getString("id")
                val email=`object`.getString("email")

                loginWithFb(name,id,email)
                /*toast(name)
                val intent=Intent(this, FbEmailActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("fbId",id)
                startActivity(intent)*/

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link,email")
        request.parameters = parameters
        request.executeAsync()
    }

    fun onLoginSuccess(user: User, isVerified: Boolean) {
        if (isVerified){
            lifecycleScope.launch {
                try {
                    authRepositry.saveUser(user)
                    val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }else{

            toast(user.verification_pin!!)
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra(RegisterActivity.REGISTERED_USER,user)
            startActivity(intent)
        }
    }

    fun onLoginFailed(msg: String) {
        toast(msg)

    }

    private fun loginWithFb(name: String, id: String, email: String) {
        lifecycleScope.launch {
            try {
                val response=authRepositry.socialLogin(name,email,id)
                Log.e("fbId::", ""+ id)
                Log.e("fb::",""+response)
                if (response.status){
                    response.data.let {
                        if (it.facebook_id == null){
                            "Oops! Registration failed".toast(this@RegisterActivity)
                            return@launch
                        }
                        onLoginSuccess(it,true)
                        //authRepositry.saveUser(it)
                        return@launch
                    }
                }else{
                    onLoginFailed(response.message)
                }

            }catch (e: ApiException){
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }catch (e: NoInternetException){
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }catch (e: Exception){
                e.printStackTrace()
                onLoginFailed(e.message!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openChromeTab(url: String) {
        val uri: Uri = Uri.parse(url)
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
        customTabsIntent.launchUrl(this, uri)
    }
    private fun isChromeInstalled(): Boolean {
        return try {
            getPackageManager().getPackageInfo("com.android.chrome", 0)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun openEventDialog() {
        category=""
        categoryIds=""
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.eve_cat_selection)
        dialog.rvcats.also { rv->
            rv.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
            rv.setHasFixedSize(true)
            catData.let {
                rv.adapter= SpinerCatAdapter(it,this,this)
            }
        }
        val send = dialog.findViewById(R.id.button53) as Button
        val ic_back = dialog.findViewById(R.id.textView131) as TextView
        val cancel = dialog.findViewById(R.id.button52) as Button
        val edt_search_category=dialog.findViewById<EditText>(R.id.edt_search_category)
        send.setOnClickListener {
            dialog.dismiss()
        }

        edt_search_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var value=s.toString()
                Log.e("valueee",value.toString())

                getEventCategories(value)

                dialog.rvcats.also { rv->

                    rv.layoutManager= LinearLayoutManager(applicationContext, RecyclerView.VERTICAL,false)
                    rv.setHasFixedSize(true)
                    catData.let {
                        rv.adapter= SpinerCatAdapter(it,this@RegisterActivity,this@RegisterActivity)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                var s=s.toString()
                Log.e("datatat",s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        cancel.setOnClickListener {

            dialog.dismiss()
            getEventCategories(" ")
            dialog.rvcats.also { rv->
                rv.layoutManager= LinearLayoutManager(applicationContext, RecyclerView.VERTICAL,false)
                rv.setHasFixedSize(true)
                catData.let {
                    rv.adapter= SpinerCatAdapter(it,this@RegisterActivity,this)
                }
            }
        }
        ic_back.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {
        if (b) {
            if (category.isEmpty()) {
                category = eveCataData.category_name
                categoryIds=eveCataData.id
            } else {
                category = category + "," + eveCataData.category_name
                categoryIds=categoryIds+","+eveCataData.id
            }
        }else{
            categoryIds=categoryIds.replace(eveCataData.id+",","")
            category=category.replace(eveCataData.category_name+",","")
        }
        binding.tvCat.text=category
    }



    companion object{
        const val REGISTERED_USER="registeredUser"
    }

    override fun onclick(Event: EventCatData) {
        binding.tvCat.text=Event.category_name
        category = Event.category_name
        categoryIds=Event.id
    }
}