package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.EventContentAdapter
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityPracticionerSelectionListBinding
import com.wiesoftware.spine.ui.home.menus.events.maps.MapviewEventsActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PracticionerSelectionListActivity : AppCompatActivity(), KodeinAware, SelectedPageClick {
    override val kodein by kodein()
    lateinit var binding: ActivityPracticionerSelectionListBinding
    lateinit var viewmodel: SearchPracticionerViewmodel
    val factory: PracticionerModelFactory by instance()
    val homeRepositry: HomeRepositry by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_practicioner_selection_list)
        viewmodel = ViewModelProvider(
            this@PracticionerSelectionListActivity,
            factory
        ).get(SearchPracticionerViewmodel::class.java)

        binding.viewmodel = viewmodel

        viewmodel.selectedPageClick = this
       viewmodel.sendDateToOther= Gson().fromJson(intent.getStringExtra("data"),SendDateToOther::class.java)
       Log.e("data",viewmodel.sendDateToOther.category)
       Log.e("data",viewmodel.sendDateToOther.location!!)
        binding.rvselecctionList.also {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            it.setHasFixedSize(true)
            it.adapter = PractSelectlistAdapter(this)
        }


    }

    override fun mapViews() {
        startActivity(Intent(this, MapviewEventsActivity::class.java))
    }
}