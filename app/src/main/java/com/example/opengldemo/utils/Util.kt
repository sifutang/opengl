package com.example.opengldemo.utils

import android.content.Context

object Util {
    fun read(assetFileName: String, context: Context): ByteArray {
        val inputStream = context.assets.open(assetFileName)
        val length = inputStream.available()
        val buffer = ByteArray(length)
        inputStream.read(buffer)
        return buffer
    }
}