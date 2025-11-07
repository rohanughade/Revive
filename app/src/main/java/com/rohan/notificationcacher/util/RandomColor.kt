package com.rohan.notificationcacher.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

fun randomColor(): Color {
    val r = (Random.nextInt(128)+127)
    val g = (Random.nextInt(128)+127)
    val b = (Random.nextInt(128)+127)
    return Color(r,g,b)
}

 fun makeImageUrl(context: Context, bitmap: Bitmap, filename: String): String{
    val file = File(context.cacheDir, "$filename.png")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream)
    stream.flush()
    stream.close()
    return file.absolutePath
}

fun isNotificationAccessGranted(context: Context): Boolean{
    val enabledListner = Settings.Secure.getString(context.contentResolver,"enabled_notification_listeners")
    return enabledListner?.contains(context.packageName) == true
}

fun requestPermission(context: Context){
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}