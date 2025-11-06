package com.rohan.notificationcacher.notificationlistener

import android.app.Notification
import android.graphics.Bitmap
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.rohan.notificationcacher.db.model.Message
import com.rohan.notificationcacher.repositery.MessageRepository
import com.rohan.notificationcacher.util.makeImageUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "MyNotificationListener"
@AndroidEntryPoint
class MyNotificationListener: NotificationListenerService() {
    @Inject
    lateinit var messageRepository: MessageRepository
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    val processNotificationId = mutableSetOf<String>()
    private val SUMMARY_MESSAGE_PATTERN = Regex("\\d+\\s+new\\s+messages?", RegexOption.IGNORE_CASE)
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


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
       val packageName = sbn?.packageName
        if (packageName =="com.whatsapp" || packageName=="com.whatsapp.w4b"){
            val extras = sbn.notification.extras
            val title = extras.getString("android.title")
            val message = extras.getCharSequence("android.text")
            val bigPicture = extras.getParcelable<Bitmap>(Notification.EXTRA_PICTURE)
            val imageurl = bigPicture?.let {
                makeImageUrl(this,it,"notif_${System.currentTimeMillis()}")
            }
            if (title == null && message==null)return

            val notoficationId = "${sbn.id}_${sbn.postTime}_${sbn.key}"
            if (processNotificationId.contains(notoficationId)){
                return
            }
            processNotificationId.add(notoficationId)

            val validqtionResult = validateMessage(message.toString())
            if (!validqtionResult)return


            val mesg = if (bigPicture!= null){
                Message(
                    sender = title.toString(),
                    message = message.toString(),
                    imgUrl = imageurl,
                    timestamp = System.currentTimeMillis()
                )
            }else{
                Message(
                    sender = title.toString(),
                    message = message.toString(),
                    timestamp = System.currentTimeMillis()

                )
            }


            serviceScope.launch {
                messageRepository.insertMessage(mesg)

            }
            Log.d(TAG, "notification posted from $title:$message")

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

    private fun validateMessage(message: String): Boolean{
        val lowerMessage = message.lowercase().trim()
        if (STATUS_PATTERNS.any { lowerMessage.contains(it) }) return false
        if (GROUP_PATTERNS.any{lowerMessage.contains(it)})return false
        if (CALL_PATTERNS.any({lowerMessage.contains(it)}))return false
        if (SUMMARY_MESSAGE_PATTERN.containsMatchIn(lowerMessage))return false

        return true
    }
}