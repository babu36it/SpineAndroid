package com.wiesoftware.spine.ui.home.menus.spine.addposts.hashtags

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.data.repo.HomeRepository


/**
 * Created by Vivek kumar on 12/28/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class HashtagViewmodel(val homeRepositry: HomeRepository) : ViewModel() {

    var hashtags: String = ""
    var hashtagEventListener: HashtagEventListener? = null

    fun onBack(view: View) {
        hashtagEventListener?.onBack()
    }

    /*
    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int){
         if (s.contains("#")){
         hashtagEventListener?.changeEditTextColor(s)
         }
    }
*/
    fun hashtagWatcher(): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence, i: Int, i1: Int, i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.contains("#")) {
                    hashtagEventListener?.changeEditTextColor(charSequence)
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        }
    }

    fun onNext(view: View) {
        if (hashtags.isEmpty()) {
            Toast.makeText(view.context, "Please enter hashtags", Toast.LENGTH_SHORT).show()
        } else {
            hashtagEventListener?.onNext(hashtags)
        }
    }
}