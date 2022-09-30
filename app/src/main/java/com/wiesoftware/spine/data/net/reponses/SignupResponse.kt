package com.wiesoftware.spine.data.net.reponses

import com.wiesoftware.spine.data.db.entities.User

data class SignupResponse(
    var status: Boolean,
    var data: User,
    var token: String,
    var token_type: String,
    var expires_in: Long,
    var message: String
)