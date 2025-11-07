package com.rohan.notificationcacher.di

import android.content.Context
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import com.rohan.notificationcacher.db.MessageDao
import com.rohan.notificationcacher.db.MessageDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun getdb(@ApplicationContext context: Context): MessageDb{
        return Room.databaseBuilder(
                context,
                MessageDb::class.java,
                "message_db"

            ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun getDao(db: MessageDb): MessageDao {
        return db.messageDao
    }
}