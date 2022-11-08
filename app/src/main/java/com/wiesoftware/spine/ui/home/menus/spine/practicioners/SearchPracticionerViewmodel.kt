package com.wiesoftware.spine.ui.home.menus.spine.practicioners

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.net.reponses.EventCatData
import com.wiesoftware.spine.data.repo.HomeRepositry

/**
 * Created by Vivek kumar on 12/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class SearchPracticionerViewmodel(val homeRepositry: HomeRepositry): ViewModel() {

    var location: String=""
    var category: String=""
    var searchEventListener: SearchEventListener?= null
    var listCat : ArrayList<EventCatData> = ArrayList()

    fun onCloseFilter(view: View){
        searchEventListener?.onclose()
    }

    fun onDeseaseClicked(view: View){
        searchEventListener?.openDialog()
    }
    fun onkeyPerformance(view: View){
        searchEventListener?.openDialog()
    }
    fun onCategoryClick(view: View){
        searchEventListener?.openDialog()
    }

   init{
       loadItem()
   }
     fun loadItem() {
        listCat.add(EventCatData("category 1","","1","","",false))
        listCat.add(EventCatData("category 2","","2","","",false))
        listCat.add(EventCatData("category 3","","3","","",false))
        listCat.add(EventCatData("category 4","","4","","",false))
        listCat.add(EventCatData("category 5","","5","","",false))
    }
     fun clearItem(){
         listCat.clear()
    }



}