package com.wiesoftware.spine.data.net.reponses

import java.io.Serializable

/**
 * Created by Vivek kumar on 5/6/2021.
 * E-mail:- vivekpcst.kumar@gmail.com
 */
data class ReviewPodData(
    val userId: String,
    val language: String,
    val category: String,
    val subcategory: String,
    val allowcomment: String
): Serializable
