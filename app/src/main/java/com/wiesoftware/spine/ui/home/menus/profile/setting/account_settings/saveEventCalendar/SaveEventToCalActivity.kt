package com.wiesoftware.spine.ui.home.menus.profile.setting.account_settings.saveEventCalendar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivitySaveEventToCalBinding
import com.wiesoftware.spine.util.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SaveEventToCalActivity : AppCompatActivity(),KodeinAware, SaveEventEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory: SaveEventViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivitySaveEventToCalBinding
    lateinit var userId: String
    var status=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_save_event_to_cal)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_save_event_to_cal)
        val viewmodel=ViewModelProvider(this,factory).get(SaveEventViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.saveEventEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
        })
        val CAL_STATUS= Prefs.getString(SaveEventToCalActivity.CAL_STATUS,"Off")
        if (CAL_STATUS.equals("On")){
            binding.switch2.isChecked=true
        }


    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onCheckedChanged(isChecked: Boolean) {
        if (isChecked){
            status=1;
        }else{
            status=0;
        }
        saveEventsToCalendarStatus(status)
    }

    private fun saveEventsToCalendarStatus(status: Int) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.saveStatusToCalendarStatus(userId,""+status)
                if (res.status){
                    getString(R.string.status_changed_successfully).toast(this@SaveEventToCalActivity)
                    val calStatus=if (status==0){"Off"}else{"On"}
                    Prefs.putAny(CAL_STATUS,calStatus)
                }else{
                    getString(R.string.oops_something_wrong).toast(this@SaveEventToCalActivity)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    companion object{
        val CAL_STATUS="calStatus"
    }
}