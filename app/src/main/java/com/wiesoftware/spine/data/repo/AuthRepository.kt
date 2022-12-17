package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.AuthApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.ProfileRes
import com.wiesoftware.spine.data.net.reponses.SignupResponse
import com.wiesoftware.spine.data.net.reponses.WelcomeResponse
import com.wiesoftware.spine.data.net.reponses.welcompageresponse.WelcomePageReponse
import okhttp3.ResponseBody

class AuthRepository(
    private val authApi: AuthApi,
    private val db: AppDatabase

) : SafeApiRequest() {

    suspend fun socialLogin(name: String, email: String, fbId: String): SignupResponse {
        return apiRequest {
            authApi.socialLoigin(
                email,
                name,
                fbId,
                "1043433501",
                "27.5454543434",
                "77.4566463434",
                "8525225435436656",
                "android"
            )
        }
    }

    suspend fun userLogin(email: String, password: String): SignupResponse {
        return apiRequest { authApi.userLogin(email, password, "vsk121231333", "android") }
    }

    suspend fun forgotPassword(email: String): ResponseBody {
        return apiRequest { authApi.forgotPassword(email) }
    }

    suspend fun userSignup(
        userName: String,
        email: String,
        password: String,
        town: String,
        category: String
    ): SignupResponse {
        return apiRequest {
            authApi.userSignup(
                email,
                userName,
                town,
                password,
                "192.168.1.1",
                "28.372128982",
                "77.327282821",
                category
            )
        }
    }

    suspend fun verifyUser(user_id: String): SignupResponse {
        return apiRequest { authApi.verifyAccount(user_id) }
    }

    suspend fun sendCode(user_id: String, phoneNumber: String): SignupResponse {
        return apiRequest { authApi.verificationCodeOnMobile(phoneNumber, user_id) }
    }



    suspend fun reSendCode(user_id: String): SignupResponse {
        return apiRequest { authApi.reSendCode( user_id) }
    }

    suspend fun getWelcomePages(): WelcomePageReponse {
        return apiRequest { authApi.getWelcomePages() }
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getUser()

    suspend fun getWelcomeData(): WelcomeResponse {
        return apiRequest { authApi.getWelcomeData() }
    }
    suspend fun getUserDetails(): ProfileRes {
        return apiRequest { authApi.getUserDetails() }
    }
}