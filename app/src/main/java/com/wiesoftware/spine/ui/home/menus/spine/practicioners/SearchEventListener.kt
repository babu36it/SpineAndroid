package com.wiesoftware.spine.ui.home.menus.spine.practicioners

interface SearchEventListener {

    fun onclose()
    fun openDialog(id: Int)
    fun openLocDialog()
    fun findPracticioners()
}