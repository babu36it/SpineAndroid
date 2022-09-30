
package com.wiesoftware.spine.ui.home.menus.spine.addposts

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * Created by Vivek kumar on 10/16/2020.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
class AddPostViewmodel: ViewModel() {

    var addPostEventListener: AddPostEventListener?= null
    fun onPostThought(view: View){
        addPostEventListener?.onPostThought()
    }
    fun onPostVideoOrPicture(view: View){
        addPostEventListener?.onPostPictureOrVideo()
    }
    fun onPostStory(view: View){
        addPostEventListener?.onPostStory()
    }
    fun onBack(view: View){
        addPostEventListener?.onBack()
    }
    fun onPostEvent(view: View){
        addPostEventListener?.onPostEvent()
    }
    fun onPostPodcast(view: View){
        addPostEventListener?.onPostPodcast()
    }

}