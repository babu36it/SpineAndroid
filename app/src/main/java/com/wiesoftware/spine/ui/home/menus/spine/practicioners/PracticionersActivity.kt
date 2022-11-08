package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPracticionersBinding
import com.wiesoftware.spine.ui.home.menus.events.addevents.MutilpeSpineCatAdapter
import kotlinx.android.synthetic.main.eve_cat_selection.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PracticionersActivity : AppCompatActivity(), KodeinAware, SearchEventListener,
    PractCatAdapter.OnEveItemChecked {
    override val kodein by kodein()
    lateinit var binding: ActivityPracticionersBinding
    lateinit var viewmodel: SearchPracticionerViewmodel
    val factory: PracticionerModelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
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

    fun openCatDialog() {
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
                 rv.adapter= PractCatAdapter(it,this)
             }
        }
        val cancel = dialog.findViewById(R.id.button52) as Button

        val textView131 = dialog.findViewById<TextView>(R.id.textView131)
        val buttonApplyFilter = dialog.findViewById<Button>(R.id.buttonApplyFilter)

        textView131.setOnClickListener {

            dialog.dismiss()
        }
        buttonApplyFilter.setOnClickListener {

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

    override fun openDialog() {
        viewmodel.clearItem()
        openCatDialog()
    }

    override fun onEventItemChecked(eveCataData: EventCatData, b: Boolean) {

    }



}