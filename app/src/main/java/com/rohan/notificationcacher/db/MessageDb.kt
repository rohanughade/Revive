package com.rohan.notificationcacher.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rohan.notificationcacher.db.model.Message

@Database(entities = [Message::class], version = 2, exportSchema = false)
abstract class MessageDb: RoomDatabase() {
    abstract val messageDao: MessageDao


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {


            }
        }
    }
}