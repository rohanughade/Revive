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

@AndroidEntryPoint
class MyNotificationListener: NotificationListenerService() {
    @Inject
    lateinit var messageRepository: MessageRepository
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val TAG = "MyNotificationListener"
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
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, "onNotificationPosted: ${messageRepository.getCount()}")
            }
        }


    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName
        Log.d(TAG, "notification removed from $packageName")

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}