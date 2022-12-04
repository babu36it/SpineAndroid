package com.wiesoftware.spine.ui.home.menus.events.addevents

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.wiesoftware.spine.databinding.ActivityAddEventSuccessBinding
import com.wiesoftware.spine.ui.home.HomeActivity
import com.wiesoftware.spine.ui.home.menus.events.addordup.AddOrDupEventActivity
import kotlinx.android.synthetic.main.activity_add_about_event.*


class AddEventSuccessActivity : AppCompatActivity(){
    var resultcode:Int=201
    private lateinit var binding:ActivityAddEventSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventSuccessBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        binding.imageButton36.setOnClickListener {

            startActivity(Intent(this@AddEventSuccessActivity, HomeActivity::class.java))
            finishAffinity()
        }

        binding.button97.setOnClickListener {
//            val intent = Intent()
//            intent.action = "Go_to_event"
//            intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
//            sendBroadcast(intent)
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("next_screen","event")
            startActivity(intent)
            finishAffinity()
        }

        binding.button98.setOnClickListener {
            finish()
            AddEventActivity.isAddNewEvent = true
        }

    }
}