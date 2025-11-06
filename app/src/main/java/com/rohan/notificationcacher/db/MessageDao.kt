package com.rohan.notificationcacher.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rohan.notificationcacher.db.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insertMessage(message: Message)

    @Query("select * from Message where sender = :sender order by timestamp desc")
     fun getMessageBySender(sender: String): Flow<List<Message>>

    @Query("select sender from Message group by sender order by max(timestamp) desc")
     fun getSenders(): Flow<List<String>>

     @Query("select count(id) from Message")
      suspend fun getCount():Int

      @Query("delete from Message where timestamp < :expiryTime")
      suspend fun deleteMessagesByTime(expiryTime: Long)

      @Delete
      suspend fun deleteMessage(message: Message)

      @Query("delete from message where sender = :sender")
      suspend fun deleteBySender(sender: String)


}