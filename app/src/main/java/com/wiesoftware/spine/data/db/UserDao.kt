package com.wiesoftware.spine.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wiesoftware.spine.data.db.entities.CURRENT_USER_ID
import com.wiesoftware.spine.data.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User):Long

    @Query("SELECT * FROM user WHERE uid= $CURRENT_USER_ID")
    fun getUser() : LiveData<User>

    @Delete
    suspend fun deleteUser(c_user: User)
}