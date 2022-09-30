package com.wiesoftware.spine.ui.home.menus.podcasts.addpodcasts

/**
 * Created by Vivek kumar on 2/1/2021.
 * Email: vivekpcst.kumar@gmail.com.
 */
interface AddPodcastEventListener {

    fun onBack()
    fun onPost(title: String, description: String)
    fun onAddPodcast()
    fun onAddImage()
    fun isAllowComment(isChecked: Boolean)
    fun onSelectCategories()
    fun onAddAditionalCategory()
    fun onAddNewCategory(category: String)
    fun onReview(title: String, description: String)
}