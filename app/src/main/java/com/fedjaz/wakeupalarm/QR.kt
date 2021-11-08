package com.fedjaz.wakeupalarm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import java.util.*
import com.google.zxing.qrcode.*
import java.io.ByteArrayOutputStream

class QR(var id: Int, var name: String, var number: Int, var location: String) {
    val WIDTH: Int = 500
    val HEIGHT: Int = 500

    var imageByteArray: ByteArray = byteArrayOf()
    var bitmap: Bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565)


    fun createImage(){
        val qrWriter = QRCodeWriter()
        val gson = Gson()
        val valuesMap = mapOf("id" to id,
            "name" to name,
            "number" to number,
            "location" to location)
        val content = gson.toJson(valuesMap)
        val bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT)

        bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565)
        for(i in 0 until WIDTH){
            for (j in 0 until HEIGHT){
                if(bitMatrix.get(i, j)){
                    bitmap.setPixel(i, j, Color.BLACK)
                }
                else{
                    bitmap.setPixel(i, j, Color.WHITE)
                }
            }
        }

        val arrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream)
        imageByteArray = arrayOutputStream.toByteArray()
    }

    fun getImage(): Bitmap{
        bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        return bitmap
    }

    fun getQrName(): String{
        return "$name #$number"
    }
}