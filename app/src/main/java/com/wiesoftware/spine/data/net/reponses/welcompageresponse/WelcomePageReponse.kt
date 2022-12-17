package com.wiesoftware.spine.data.net.reponses.welcompageresponse

data class WelcomePageReponse(
    val `data`: List<Data>,
    val imgURL: String,
    val message: String,
    val status: Boolean
){
    data class Data(
        val backgroundImage: String,
        val createdDate: String,
        val description: String,
        val id: String,
        val sortOrder: String,
        val status: String,
        val title: String
    )
}