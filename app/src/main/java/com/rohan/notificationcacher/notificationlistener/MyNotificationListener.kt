package com.rohan.notificationcacher.notificationlistener

import android.app.Notification
import android.graphics.Bitmap
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rohan.notificationcacher.db.model.Message
import com.rohan.notificationcacher.db.model.SenderInfo
import com.rohan.notificationcacher.repositery.MessageRepository
import com.rohan.notificationcacher.util.makeImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


private const val TAG = "MyNotificationListener"
@AndroidEntryPoint
class MyNotificationListener: NotificationListenerService() {
    @Inject
    lateinit var messageRepository: MessageRepository
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    val processNotificationId = mutableSetOf<String>()
    companion object {
        private val SUMMARY_MESSAGE_PATTERN =
            Regex("\\d+\\s+new\\s+messages?", RegexOption.IGNORE_CASE)
        private val CALL_PATTERNS = setOf(
            "calling...",
            "calling",
            "ringing...",
            "ringing",
            "ongoing call",
            "ongoing voice call",
            "ongoing video call",
            "call ended",
            "missed call",
            "declined call",
            "call in progress",
            "incoming call"
        )
        private val STATUS_PATTERNS = setOf(
            "checking for new messages",
            "new messages",
            "waiting for this message",
            "this message was deleted",
            "you deleted this message",
            "message deleted",
            "typing...",
            "recording audio...",
            "recording voice message",
            "sync..."
        )
        private val GROUP_PATTERNS = setOf(
            "you were added",
            "joined using this group's invite link",
            "left",
            "removed",
            "group created",
            "subject changed",
            "icon changed",
            "description changed"
        )
    }


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
       val packageName = sbn?.packageName
        if (packageName =="com.whatsapp" || packageName=="com.whatsapp.w4b"){

             serviceScope.launch {
                 notificationProcess(sbn = sbn)
             }
        }


    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName
        val notoficationId = "${sbn?.id}_${sbn?.postTime}_${sbn?.key}"
        processNotificationId.remove(notoficationId)
        Log.d(TAG, "notification removed from $packageName")

    }

    override fun onDestroy() {
        super.onDestroy()
        processNotificationId.clear()
        serviceJob.cancel()

    }

    suspend fun notificationProcess(sbn: StatusBarNotification){
        val extras = sbn.notification.extras
        val title = getTitle(extras = extras)
        val message = getMessage(extras = extras)
        val imageUrl= withContext(Dispatchers.IO){
            val bigPicture = extras.getParcelable<Bitmap>(Notification.EXTRA_PICTURE)
            bigPicture?.let {
                makeImageUrl(this@MyNotificationListener,it,"notif_${System.currentTimeMillis()}")

            }
        }
        if (title.isBlank() || message.isBlank())return

        val notoficationId = "${sbn.id}_${sbn.postTime}_${sbn.key}"
        if (processNotificationId.contains(notoficationId)){
            return
        }
        processNotificationId.add(notoficationId)

        val validationResult = validateMessage(message)
        if (!validationResult)return

        val senderInfo = parseSenderInfo(title = title)

        val twoMinutesAgo = System.currentTimeMillis() - (2 * 60 * 1000)
        val existingMessage = withContext(Dispatchers.IO) {
            messageRepository.getMessageByContent(
                sender =  senderInfo.groupOrContact,
                message = message,
                fromTime = twoMinutesAgo
            )
        }

        if (existingMessage != null) {
            Log.d(TAG, "Duplicate detected in database (within 2 minutes)")
            return
        }

        val mesg =
            Message(
                sender = senderInfo.groupOrContact,
                senderName =senderInfo.individualSender ,
                message = message.trim(),
                imgUrl = imageUrl,
                timestamp = sbn.postTime
            )

        withContext(Dispatchers.IO){
            messageRepository.insertMessage(mesg)
            Log.d(TAG, "notificationProcess: update")

        }



    }

    private fun getTitle(extras: Bundle): String{
        var title = extras.getString(Notification.EXTRA_TITLE)
        if (!title.isNullOrBlank())return title

        title = extras.getString(Notification.EXTRA_CONVERSATION_TITLE)
        if (!title.isNullOrBlank())return title

        title = extras.getString(Notification.EXTRA_SUB_TEXT)
        if (!title.isNullOrBlank())return title

        title = extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
        if (!title.isNullOrBlank())return title

        return ""
    }

    private fun getMessage(extras: Bundle): String{
        var message = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        if (!message.isNullOrBlank())return message

        message = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        if (!message.isNullOrBlank())return message

        message = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()
        if (!message.isNullOrBlank())return message

        message = extras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()
        if (!message.isNullOrBlank())return message

        return ""
    }
    private fun normalizeSender(title: String): String {
        return title
            .replace(Regex("\\(\\s*\\d+\\s*(new\\s+)?messages?\\s*\\)", RegexOption.IGNORE_CASE), "")
            .replace(Regex("from\\s+\\d+\\s+chats?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s*new messages?$", RegexOption.IGNORE_CASE), "")
            .trim()

    }
    private fun validateMessage(message: String): Boolean{
        val lowerMessage = message.lowercase().trim()
        if (STATUS_PATTERNS.any { lowerMessage.contains(it) }) return false
        if (GROUP_PATTERNS.any{lowerMessage.contains(it)})return false
        if (CALL_PATTERNS.any({lowerMessage.contains(it)}))return false
        if (SUMMARY_MESSAGE_PATTERN.containsMatchIn(lowerMessage))return false
        if (Regex("\\d+\\s*(new\\s+)?messages?(\\s+from\\s+\\d+\\s+chats?)?", RegexOption.IGNORE_CASE).containsMatchIn(lowerMessage)) return false

        if (lowerMessage.contains("from") && lowerMessage.contains("chats")) return false

        if (lowerMessage == "message deleted" || lowerMessage == "checking for new messages") return false

        return true
    }

    private fun parseSenderInfo(title: String): SenderInfo{
        val cleaned = normalizeSender(title = title)
        val delimiterIndex = cleaned.indexOfAny(charArrayOf(':', '~'))
        return if (delimiterIndex != -1){
            val groupName = cleaned.substring(0,delimiterIndex).trim()
            val individualName = cleaned.substring(delimiterIndex+1).trim()
            SenderInfo(
                groupName,
                individualName.ifBlank { null }
            )
        }else{
            SenderInfo(
                cleaned.trim(),
                null
            )
        }
    }

}