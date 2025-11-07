package com.rohan.notificationcacher.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val sender: String,
    val senderName: String?=null,
    val message: String,
    val imgUrl: String?=null,
    val timestamp: Long = System.currentTimeMillis()

)