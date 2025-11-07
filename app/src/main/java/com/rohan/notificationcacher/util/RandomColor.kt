package com.rohan.notificationcacher.util

import android.content.Context
import android.graphics.Bitmap
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

suspend fun makeImageUrl(context: Context, bitmap: Bitmap, filename: String): String{
    val file = File(context.cacheDir, "$filename.png")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream)
    stream.flush()
    stream.close()
    return file.absolutePath
}

fun toneDownColor(color: Color,alpha: Float = 0.6f,factor: Float = 0.8f): Color{
    return color.copy(
        red = color.red*factor,
        green = color.green*factor,
        blue = color.blue*factor,
        alpha = alpha
    )
}