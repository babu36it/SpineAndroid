package com.wiesoftware.spine.data.repo

import android.util.Log
import com.wiesoftware.spine.data.db.AppDatabase
import com.wiesoftware.spine.data.db.entities.User
import com.wiesoftware.spine.data.net.Api
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.ProfileRes
import com.wiesoftware.spine.data.net.reponses.SignupResponse
import com.wiesoftware.spine.data.net.reponses.SingleRes
import com.wiesoftware.spine.util.Prefs
import okhttp3.ResponseBody

class AuthRepositry(
    private val api: Api,
    private val db: AppDatabase

) : SafeApiRequest() {

    suspend fun socialLogin(name: String,email: String,fbId: String): SignupResponse{
        Log.e("email::",email+","+fbId+","+name)
        return apiRequest { api.socialLoigin(email,name,fbId,"1043433501","27.5454543434","77.4566463434","8525225435436656","android") }
    }

    suspend fun userLogin(email: String,password: String): SignupResponse{
        return apiRequest { api.userLogin(email,password,"vsk121231333","android") }
    }
    suspend fun forgotPassword(email: String): ResponseBody{
        return apiRequest { api.forgotPassword(email) }
    }

    suspend fun userSignup(userName: String,email: String,password:String,town: String,category:String) : SignupResponse{
        return apiRequest { api.userSignup(email,userName,town,password,"192.168.1.1","28.372128982","77.327282821",category)}
    }
    suspend fun verifyUser(user_id: String):SignupResponse{
        return apiRequest { api.verifyAccount(Prefs.getString("AuthToken", "")!!, user_id) }
    }
    suspend fun sendCode(user_id:String ,phoneNumber:String): SignupResponse{
        return apiRequest { api.verificationCodeOnMobile(phoneNumber,user_id) }
    }

    suspend fun getUserDetails(): SignupResponse {
        return apiRequest { api.getUserDetailAuth(Prefs.getString("AuthToken", "")!! ) }
    }

    suspend fun reSendCode(user_id:String ): SignupResponse {
        return apiRequest { api.reSendCode(Prefs.getString("AuthToken", "")!!, user_id ) }
    }

    suspend fun saveUser(user: User) =db.getUserDao().upsert(user)

    fun getUser() =db.getUserDao().getUser()

}