package com.rohan.notificationcacher.repositery

import com.rohan.notificationcacher.db.MessageDao
import com.rohan.notificationcacher.db.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(private val dao: MessageDao){

     fun getAllUser(): Flow<List<String>> = dao.getSenders()

     fun getMessagesBySender(sender: String): Flow<List<Message>> = dao.getMessageBySender(sender)

    suspend fun insertMessage(message: Message) = dao.insertMessage(message)

    suspend fun  deleteMessageByTime(expiryTime: Long) = dao.deleteMessagesByTime(expiryTime)

    suspend fun deleteMessage(message: Message) = dao.deleteMessage(message)

    suspend fun deleteMessageByUser(sender: String) = dao.deleteBySender(sender)
}