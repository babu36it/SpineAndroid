package com.wiesoftware.spine.data.repo

import com.wiesoftware.spine.data.net.SafeApiRequest
import com.wiesoftware.spine.data.net.SettingsApi
import com.wiesoftware.spine.data.net.reponses.CurrencyRes
import com.wiesoftware.spine.data.net.reponses.LangRes
import com.wiesoftware.spine.data.net.reponses.SingleRes

class SettingsRepository(
    private val settingsApi: SettingsApi
) : SafeApiRequest() {

    suspend fun deleteAccount(): SingleRes {
        return apiRequest { settingsApi.deleteAccount() }
    }

    suspend fun deactivateAccount(): SingleRes {
        return apiRequest { settingsApi.deactivateAccount() }
    }

    suspend fun requestToChangeEmail(email: String): SingleRes {
        return apiRequest { settingsApi.requestToChangeEmail(email) }
    }

    suspend fun saveStatusToCalendarStatus(calender_status: String): SingleRes {
        return apiRequest { settingsApi.saveStatusToCalendarStatus(calender_status) }
    }

    suspend fun whoCanMessage(message_auth: String): SingleRes {
        return apiRequest { settingsApi.whoCanMessage(message_auth) }
    }

    suspend fun getCurrency(): CurrencyRes {
        return apiRequest { settingsApi.getCurrency() }
    }

    suspend fun getLanguages(): LangRes {
        return apiRequest { settingsApi.getLanguages() }
    }

    suspend fun getEmailNotification(
        e_notify_status: String,
        e_event_attach_status: String,
        e_message_status: String,
        e_comment_reply_status: String,
        e_event_podcast_status: String,
        e_update_from_spine_status: String,
        e_spine_surveys_status: String
    ): SingleRes {
        return apiRequest {
            settingsApi.getEmailNotification(
                e_notify_status, e_event_attach_status,
                e_message_status, e_comment_reply_status,
                e_event_podcast_status, e_update_from_spine_status,
                e_spine_surveys_status
            )
        }
    }

    suspend fun getMobileNotifications(
        m_notify_status: String,
        m_like_notify_status: String,
        m_comment_notify_status: String,
        m_update_and_reminders_status: String,
        m_save_event_reminders_status: String,
        m_message_status: String,
        m_follow_status: String,
        m_spine_impulse_status: String,
        m_any_post_status: String
    ): SingleRes {
        return apiRequest {
            settingsApi.getMobileNotifications(
                m_notify_status,
                m_like_notify_status,
                m_comment_notify_status,
                m_update_and_reminders_status,
                m_save_event_reminders_status,
                m_message_status,
                m_follow_status,
                m_spine_impulse_status,
                m_any_post_status
            )
        }
    }
    suspend fun getPrivarcySettings(
        p_findability: String,
        p_customization: String,
        p_necessary: String,
        p_personalized: String
    ): SingleRes {
        return apiRequest {
            settingsApi.getPrivarcySettings(
                p_findability, p_customization, p_necessary,
                p_personalized
            )
        }
    }
}