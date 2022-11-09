package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SearchPracticionerViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var location: String=""
    var category: String=""
    var keyPerformance: String=""
    var desease: String=""
    var searchEventListener: SearchEventListener?= null
    var selectedPageClick: SelectedPageClick?= null
    var listCat : ArrayList<PractiCatDDataTemp> = ArrayList()
    var listLocation : ArrayList<LocationModel> = ArrayList()
    lateinit var sendDateToOther: SendDateToOther
    fun onCloseFilter(view: View){
        searchEventListener?.onclose()
    }

    fun onDeseaseClicked(view: View){
        searchEventListener?.openDialog(view.id)
    }
    fun onkeyPerformance(view: View){
        searchEventListener?.openDialog(view.id)
    }
    fun onCategoryClick(view: View){
        searchEventListener?.openDialog(view.id)
    }
    fun onLocationClick(view: View){
        searchEventListener?.openLocDialog()
    }
    fun findPracticioner(view: View){
        searchEventListener?.findPracticioners()
    }
    fun mapView(view: View){
        selectedPageClick?.mapViews()
    }


   init{
       loadItem()
       loadLocation()
   }

     fun loadLocation() {
        listLocation.add(LocationModel("Location 1",))
        listLocation.add(LocationModel("Location 2",))
        listLocation.add(LocationModel("Location 3",))
        listLocation.add(LocationModel("Location 4",))
        listLocation.add(LocationModel("Location 5",))
    }

    fun loadItem() {
        listCat.add(PractiCatDDataTemp("category 1","","1","","",false,""))
        listCat.add(PractiCatDDataTemp("category 2","","2","","",false,""))
        listCat.add(PractiCatDDataTemp("category 3","","3","","",false,""))
        listCat.add(PractiCatDDataTemp("category 4","","4","","",false,""))
        listCat.add(PractiCatDDataTemp("category 5","","5","","",false,""))
    }
     fun clearItem(){
         listCat.clear()
    }



}