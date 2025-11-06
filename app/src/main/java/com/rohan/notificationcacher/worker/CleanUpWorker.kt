package com.rohan.notificationcacher.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohan.notificationcacher.repositery.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CleanUpWorker @AssistedInject constructor(
    @Assisted appContext: Context
    , @Assisted params: WorkerParameters,
    private val messageRepository: MessageRepository) : CoroutineWorker(appContext, params) {


    override suspend fun doWork(): Result {
       return withContext(Dispatchers.IO){
           return@withContext try {
               val currentTime = System.currentTimeMillis()
               val expiryTime = currentTime- TimeUnit.DAYS.toMillis(1)
               messageRepository.deleteMessageByTime(expiryTime)
               Result.success()
           }catch (e: Exception){
               Result.failure()
           }
        }
    }
}