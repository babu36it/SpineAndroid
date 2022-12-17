package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.net.ProfileApi
import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.reponses.SingleRes
import okhttp3.MultipartBody

data class ProfileRepository(
    private val profileApi: ProfileApi,
) : SafeApiRequest() {

    suspend fun editProfille(
        account_type: String,
        listing_type: String,
        name: String,
        display_name: String,
        bio: String,
        category: String,
        interested: String,
        offer_desciption: String,
        key_perfomance: String,
        desease_pattern: String,
        languages: String,
        qualification: String,
        company_name: String,
        street_1: String,
        street_2: String,
        street_3: String,
        city: String,
        postcode: String,
        country: String,
        address: String,
        metaverse_address: String,
        website: String,
        contact_email: String,
        business_phone: String,
        business_mobile: String,
        business_phone_code: String,
        business_mobile_code: String


    ): SingleRes {
        return apiRequest {
            profileApi.editProfile(
                account_type,
                listing_type,
                name,
                display_name,
                bio,
                category,
                interested, offer_desciption, key_perfomance,
                desease_pattern, languages, qualification,
                company_name, street_1, street_2, street_3, city,
                postcode, country, address, metaverse_address, website,
                contact_email, business_phone, business_mobile,
                business_phone_code, business_mobile_code
            )
        }
    }

    suspend fun updateUserProfilePic(image: MultipartBody.Part?): SingleRes {
        return apiRequest { profileApi.updateUserProfilePic(image) }
    }

    suspend fun updateUserBgProfilePic(image: MultipartBody.Part): SingleRes {
        return apiRequest { profileApi.updateUserBgProfilePic(image) }
    }

}