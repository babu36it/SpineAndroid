package com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.HashtagAutocompleteAdapter
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityHashtagBinding
import com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.autosearchfrag.AutoSearchFragment
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.COLOR_ID
import com.wiesoftware.spine.ui.home.menus.spine.addposts.postthought.THOUGHT
import com.wiesoftware.spine.ui.home.menus.spine.addposts.reviewpost.ReviewPostActivity
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

val HASHTAG: String="hashtag"
class HashtagActivity : AppCompatActivity(),KodeinAware, HashtagEventListener,
    HashtagAutocompleteAdapter.OnHashtagSelectedListener,
    AutoSearchFragment.OnHashSelectedListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: HashtagViewmodelFactory by instance()
    lateinit var binding: ActivityHashtagBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_hashtag)
        val viewmodel=ViewModelProvider(this, factory).get(HashtagViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.hashtagEventListener=this
        getHashtags()
        binding.etHashtag.setOnClickListener {
            //openHashtagDialog()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.searchAuto, AutoSearchFragment(), "spine search")
                .commit()
        }
        binding.editTextTextPersonName32.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.button39.visibility = View.VISIBLE
                binding.etHashtag.isEnabled=true
                binding.searchAuto.visibility=View.GONE
            }
        })
        /*binding.editTextTextPersonName32.setOnClickListener {
            binding.button39.visibility = View.VISIBLE
            binding.etHashtag.isEnabled=true
            binding.searchAuto.visibility=View.GONE
        }*/
        binding.etHashtag.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){

                    binding.etHashtag.isEnabled = false
                    binding.button39.visibility = View.INVISIBLE
                    //openHashtagDialog()
                    supportFragmentManager
                        .beginTransaction()
                        .add(R.id.searchAuto, AutoSearchFragment(), "spine search")
                        .commit()

            }
        }

    }
    private fun getHashtags() {
        lifecycleScope.launch {
            val hastagRes= homeRepositry.getHashtagList()
            if (hastagRes.status){
                hashtagDataList=hastagRes.data
            }
        }
    }

    lateinit var hashtagName: TextView
    lateinit var hashAdapter: HashtagAutocompleteAdapter
    var hashtagDataList: MutableList<HashtagData> = mutableListOf()
    fun openHashtagDialog() {
        //binding.etHashtag.isEnabled=false
        supportFragmentManager
            .beginTransaction()
            .add(R.id.searchAuto, AutoSearchFragment(), "spine search")
            .commit()

       /* val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.open_hashtag_dialog)
        hashtagName=dialog.findViewById(R.id.textView267) as TextView
        val add = dialog.findViewById(R.id.Button71) as Button
        val cancel = dialog.findViewById(R.id.imageButton70) as ImageButton
        val etSearch = dialog.findViewById(R.id.editTextTextPersonName27) as EditText
        val rvHashtagList=dialog.findViewById(R.id.rvHashtagList) as RecyclerView
        hashAdapter= HashtagAutocompleteAdapter(hashtagDataList,this)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hashAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        rvHashtagList.also {
            it.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter=hashAdapter

        }
        add.setOnClickListener {

            dialog.dismiss()
        }
        cancel.setOnClickListener { dialog.dismiss() }


        dialog.show()*/
    }

    var selectedHashtags=""
    override fun onHashtagSelected(hashtagData: HashtagData) {
        val hashtag=if (hashtagData.hash_title.contains("#")){
            hashtagData.hash_title
        }else{
            "#"+hashtagData.hash_title
        }

        if (selectedHashtags.isEmpty()){
            selectedHashtags=hashtag
        }else{
            selectedHashtags=selectedHashtags+",\n"+hashtag
        }
        hashtagName.text=selectedHashtags
        binding.etHashtag.setText(selectedHashtags)
    }


    override fun onBack() {
        onBackPressed()
    }

    override fun onNext(hashtags: String) {
        Log.e("hash::", hashtags)
        val color=intent.getStringExtra(COLOR_ID)
        val thought=intent.getStringExtra(THOUGHT)
        val intent=Intent(this, ReviewPostActivity::class.java)
        intent.putExtra(COLOR_ID, color)
        intent.putExtra(THOUGHT, thought)
        intent.putExtra(HASHTAG, hashtags)
        startActivity(intent)
    }

    override fun changeEditTextColor(s: CharSequence) {
        binding.etHashtag.setTextColor(ContextCompat.getColor(this, R.color.blue))
        binding.etHashtag.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onHashSelected(hashtagData: HashtagData) {
        binding.button39.visibility = View.VISIBLE
        val hashtag=if (hashtagData.hash_title.contains("#")){
            hashtagData.hash_title
        }else{
            "#"+hashtagData.hash_title
        }
        if (selectedHashtags.isEmpty()){
            selectedHashtags=hashtag
        }else{
            selectedHashtags=selectedHashtags+",\n"+hashtag
        }
        //hashtagName.text=selectedHashtags
        binding.etHashtag.setText(selectedHashtags)
        binding.etHashtag.isEnabled=true


    }

    override fun onCloseFragment() {

        binding.button39.visibility = View.VISIBLE
        binding.etHashtag.isEnabled=true
    }
}