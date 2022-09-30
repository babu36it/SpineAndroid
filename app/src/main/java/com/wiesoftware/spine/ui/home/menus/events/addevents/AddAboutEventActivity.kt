package com.wiesoftware.spine.ui.home.menus.events.addevents

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wiesoftware.spine.R
import com.wiesoftware.spine.databinding.ActivityAddAboutEventBinding
import kotlinx.android.synthetic.main.activity_add_about_event.*


class AddAboutEventActivity : AppCompatActivity() {
    var resultcode:Int=201
    private lateinit var binding:ActivityAddAboutEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAboutEventBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {

            showPopup()

        }

        binding.imageButton16.setOnClickListener {
            onBackPressed()
        }
    }

    fun showPopup(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.save_change))
        builder.setMessage(getString(R.string.save_details))
        builder.setPositiveButton(getString(R.string.save)) { dialog, which ->
            val intent = Intent()
            intent.putExtra("about", edt_about.text.toString())
            setResult(Activity.RESULT_OK,intent);
            finish()
        }
        builder.setNegativeButton(R.string.discard) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }


}