package com.wiesoftware.spine.ui.home.menus.profile.masseges.eve_request

import android.os.Bundle
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
import com.wiesoftware.spine.data.adapter.EventRequestUsersAdapter
import com.wiesoftware.spine.data.net.reponses.EventRequestData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.FragmentEveRequestBinding
import com.wiesoftware.spine.ui.home.menus.spine.foryou.STORY_IMAGE
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.toast
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EveRequestFragment : Fragment(),KodeinAware, EventRequestEventListener,
    EventRequestUsersAdapter.EventRequestUserListener {

    override val kodein by kodein()
    val homeRepositry: HomeRepositry by instance()
    val factory: EventRequestViewmodelFactory by instance()
    lateinit var binding: FragmentEveRequestBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_eve_request,container,false)
        val viewmodel=ViewModelProvider(this,factory).get(EventRequestViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.eventRequestEventListener=this
        viewmodel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user->
            userId=user.users_id!!
            getEventRequest()
        })

        return  binding.root//inflater.inflate(R.layout.fragment_eve_request, container, false)
    }

    private fun getEventRequest() {

        val dataList= arrayListOf<EventRequestData>()

        dataList.add(
            EventRequestData( "bio: String",
                "2",
                "Online Meditation",
                "event_id: String",
                "event_user_id: String",
                "file: String,",
                "id: String",
                "9 May 2020 • 20:00",
                " multiple: String,",
                "name: String",
                " profile_pic: String",
                "Craig Warner requests to join your event.",
                "town: String",
                "type: String",
                "users_id: String")
        )

        dataList.add(
            EventRequestData( "bio: String",
                "3",
                "Online Meditation",
                "event_id: String",
                "event_user_id: String",
                "file: String,",
                "id: String",
                "25 May 2020 • 20:00",
                " multiple: String,",
                "name: String",
                " profile_pic: String",
                "Craig Warner requests to join your event.",
                "town: String",
                "type: String",
                "users_id: String")
        )
        binding.rvEveRequests.also {
            it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            it.setHasFixedSize(true)
            it.adapter=EventRequestUsersAdapter(dataList,this@EveRequestFragment)
        }


        /*remove return for all Api */
        return



        lifecycleScope.launch {
            try {
                val res=homeRepositry.getEventRequestUserList(1,100,userId)
                if (res.status){
//                    STORY_IMAGE=res.profile_image
//                    val dataList=res.data
                    val dataList= arrayListOf<EventRequestData>()

                    dataList.add(
                        EventRequestData( "bio: String",
                            "0",
                            "email: String",
                            "event_id: String",
                            "event_user_id: String",
                            "file: String,",
                            "id: String",
                            "message: String",
                           " multiple: String,",
                            "name: String",
                           " profile_pic: String",
                            "title: String",
                            "town: String",
                            "type: String",
    "users_id: String")
                    )
                    binding.rvEveRequests.also {
                        it.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=EventRequestUsersAdapter(dataList,this@EveRequestFragment)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onAcceptRequest(eveRequestData: EventRequestData) {
        val eveId=eveRequestData.id
        changeBookingstatus(eveId,"2","Request accepted!")
    }

    override fun onDeclienedRequest(eveRequestData: EventRequestData) {
        val eveId=eveRequestData.id
        changeBookingstatus(eveId,"3","Request declined!")
    }

    private fun changeBookingstatus(s: String, s1: String,msg: String) {
        lifecycleScope.launch {
            try {
                val res=homeRepositry.changeBookingStatus(""+s,""+s1)
                if (res.status){
                    msg.toast(requireContext())
                    getEventRequest()
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
}