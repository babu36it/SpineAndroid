package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.ActivityPracticionersBinding

import kotlinx.android.synthetic.main.eve_cat_selection.*
import kotlinx.android.synthetic.main.practicioner_location_selection.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PracticionersActivity : AppCompatActivity(), KodeinAware, SearchEventListener,
    PractCatAdapter.OnEveItemChecked, PractLocAdapter.OnItemChecked {
    override val kodein by kodein()
    lateinit var binding: ActivityPracticionersBinding
    lateinit var viewmodel: SearchPracticionerViewmodel
    val factory: PracticionerModelFactory by instance()
    val homeRepositry: HomeRepository by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_practicioners)
        viewmodel = ViewModelProvider(
            this@PracticionersActivity,
            factory
        ).get(SearchPracticionerViewmodel::class.java)
        binding.viewmodel = viewmodel
        viewmodel.searchEventListener = this
    }

    fun openCatDialog(id: Int) {
        viewmodel.loadItem()
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.practicioner_cat_selection)
        dialog.rvcats.also { rv ->
            rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            viewmodel. listCat.let {
                 rv.adapter= PractCatAdapter(it,this,id)
             }
        }
        val cancel = dialog.findViewById(R.id.button52) as Button

        val textView131 = dialog.findViewById<TextView>(R.id.textView131)
        val buttonApplyFilter = dialog.findViewById<Button>(R.id.buttonApplyFilter)
        var joinString : ArrayList<String> =ArrayList()
        textView131.setOnClickListener {

            dialog.dismiss()
        }
        buttonApplyFilter.setOnClickListener {
            dialog.rvcats.adapter!!.notifyDataSetChanged()
            var tempFrom =""
            for (list in viewmodel.listCat){
                   if(list.isSelect){
                       joinString.add(list.category_name)
                       tempFrom=list.fromClick.toString()
                   }
            }
            when(tempFrom.toInt())
            {
                binding.editTextCategory.id->{
                    binding.editTextCategory.setText(joinString.joinToString())
                    viewmodel.category=joinString.joinToString()
                }
                binding.editTextDeasesPattern.id->{
                    binding.editTextDeasesPattern.setText(joinString.joinToString())
                    viewmodel.desease=joinString.joinToString()
                }
                binding.editTextKEyPerformance.id->{
                    binding.editTextKEyPerformance.setText(joinString.joinToString())
                    viewmodel.keyPerformance=joinString.joinToString()
                }

            }
           joinString.joinToString()
            Log.e("jointostring",viewmodel.category)
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onclose() {
          finish()
    }

    override fun openDialog(id: Int) {
        viewmodel.clearItem()
        openCatDialog(id)
    }

    override fun onEventItemChecked(eveCataData: PractiCatDDataTemp, b: Boolean) {


    }
    lateinit var dialog: Dialog
    override fun openLocDialog() {
         dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.practicioner_location_selection)
        dialog.rvLocation.also { rv ->
            rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            rv.setHasFixedSize(true)
            viewmodel. listLocation.let {
                rv.adapter= PractLocAdapter(it,this)
            }
        }

        val textView131 = dialog.findViewById<TextView>(R.id.textView131)
        textView131.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }

    override fun findPracticioners() {
        startActivity(Intent(this,PracticionerSelectionListActivity::class.java)
            .putExtra("data",Gson().toJson(SendDateToOther(viewmodel.location,viewmodel.keyPerformance,viewmodel.desease,viewmodel.category)))
        )
    }

    override fun onItemChecked(item: LocationModel) {
             binding.editTextByCity.setText(item.location.toString())
             viewmodel.keyPerformance=item.location.toString()
             if(dialog.isShowing){
                 dialog.dismiss()
             }

    }

}

data class SendDateToOther(var location :String?,var keypermance:String,var desease: String,var category:String)