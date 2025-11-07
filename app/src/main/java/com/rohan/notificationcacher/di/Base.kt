package com.rohan.notificationcacher.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rohan.notificationcacher.worker.CleanUpWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Base: Application(), Configuration.Provider{
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =  Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    override fun onCreate() {
        super.onCreate()
        cleanupWorker()

    }




    private fun cleanupWorker() {
        try {
            val workerManager = WorkManager.getInstance(this)

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()


            val workBuilder = PeriodicWorkRequestBuilder<CleanUpWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()



            workerManager.enqueueUniquePeriodicWork(
                "message cleanup",
                ExistingPeriodicWorkPolicy.KEEP,
                workBuilder
            )
        }catch (e: Exception){
            e.printStackTrace()
        }



    }
}

