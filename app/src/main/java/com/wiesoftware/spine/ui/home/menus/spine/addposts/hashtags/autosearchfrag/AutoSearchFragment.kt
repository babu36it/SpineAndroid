package com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags.autosearchfrag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.data.adapter.HashtagAutocompleteAdapter
import com.wiesoftware.spine.data.net.reponses.HashtagData
import com.wiesoftware.spine.data.repo.HomeRepository
import com.wiesoftware.spine.databinding.FragmentAutoSearchBinding
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.showKeyboard
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class AutoSearchFragment : Fragment(),KodeinAware,
    HashtagAutocompleteAdapter.OnHashtagSelectedListener, AutoSearchEventListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepository by instance()
    lateinit var binding: FragmentAutoSearchBinding
    var userId: String=""
    var dataList: MutableList<HashtagData> = mutableListOf()
    var onHashSelectedListener: OnHashSelectedListener? = null
    lateinit var adapter: HashtagAutocompleteAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onHashSelectedListener=activity as OnHashSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_auto_search, container, false)
        val viewmodel=ViewModelProvider(this).get(AutoSearchViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.autoSearchEventListener=this
        homeRepositry.getUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getUserHashtags()
        })
        binding.recyclerView7.visibility = View.GONE
        binding.editTextTextPersonName28.requestFocus()
        showKeyboard()
        binding.editTextTextPersonName28.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.recyclerView7.visibility = View.VISIBLE
                adapter.filter.filter(s)
                binding.tvNewHashtag.text="#"+s
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.tvNewHashtag.setOnClickListener {
            val title= binding.tvNewHashtag.text.toString()
            val hashtagData=HashtagData("",title,"","","")
            onHashSelectedListener?.onHashSelected(hashtagData)
            onCloseAutosearch()
        }

        return binding.root
    }

    private fun getUserHashtags() {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.getUserHashtags()
                if (res.status){
                    dataList=res.data
                    adapter= HashtagAutocompleteAdapter(dataList,this@AutoSearchFragment)
                    binding.recyclerView7.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onHashtagSelected(hashtagData: HashtagData) {
        onHashSelectedListener?.onHashSelected(hashtagData)
        onCloseAutosearch()
    }

    override fun onCloseAutosearch() {
        activity?.getSupportFragmentManager()!!.beginTransaction().remove(this).commit();

        //activity?.onBackPressed()
//        val fragment: Fragment? = activity?.supportFragmentManager?.findFragmentByTag("spinesearch")
//        if (fragment != null) activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)
//            ?.commit()

        //activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
       onHashSelectedListener?.onCloseFragment()
    }
    interface OnHashSelectedListener{
        fun onHashSelected(hashtagData: HashtagData)
        fun onCloseFragment()
    }
}