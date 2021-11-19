package com.fedjaz.wakeupalarm

import android.graphics.*
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.*
import java.io.ByteArrayOutputStream
import com.itextpdf.*
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable


class QR(var id: Int = -1, var name: String, var number: Int, var location: String) : Serializable {
    @Transient var checked = false
    var imageByteArray: ByteArray = byteArrayOf()
    @Transient var bitmap: Bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.RGB_565)
    var isImageCreated = false


    fun createImage(){
        val qrWriter = QRCodeWriter()
        val gson = Gson()
        val valuesMap = mapOf("id" to id,
            "name" to name,
            "number" to number,
            "location" to location)
        val content = gson.toJson(valuesMap)
        val bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT)

        bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.RGB_565)
        for(i in 0 until QR_WIDTH){
            for (j in 0 until QR_HEIGHT){
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
        isImageCreated = true
    }

    fun initializeImage(){
        bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    fun getQrName(): String{
        return "$name #$number"
    }

    fun createPrintableImage(width: Int, height: Int): Bitmap{
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        val qrMarginTop = 0.1 * width
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)
        canvas.drawBitmap(this.bitmap, (width / 2 - QR_WIDTH / 2).toFloat(), qrMarginTop.toFloat(), null)

        val textPaint = Paint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 50f
        val textMarginTop = 0.1 * width
        var currentMargin = (qrMarginTop + QR_WIDTH + textMarginTop).toFloat()
        canvas.drawText(this.name, (width / 2).toFloat(), currentMargin, textPaint)
        currentMargin += width * 0.15f
        canvas.drawText("#${this.number}", (width / 2).toFloat(), currentMargin, textPaint)
        currentMargin += width * 0.15f
        canvas.drawText(this.location, (width / 2).toFloat(), currentMargin, textPaint)

        return bitmap
    }

    companion object {
        const val QR_WIDTH: Int = 420
        const val QR_HEIGHT: Int = 420
        private const val PAGE_WIDTH: Int = 2880
        private const val PAGE_HEIGHT: Int = 4060

        fun createPdf(qrs: MutableList<QR>, dir: File): File{
            val document = Document(Rectangle(595f, 842f))

            val file = File.createTempFile("temppdf", ".pdf", dir)

            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()
            document.setMargins(0f, 0f, 0f, 0f)

            var pages = 0
            for((i, qr) in qrs.withIndex()){
                if(i / 4 > pages){
                    document.newPage()
                    pages = i / 4
                }
                val x: Int = i % 2
                val y: Int = i / 2
                val bitmap = qr.createPrintableImage(PAGE_WIDTH / 4, PAGE_HEIGHT / 4)
                val arrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream)
                val bytes = arrayOutputStream.toByteArray()

                val image = Image.getInstance(bytes)
                image.compressionLevel = 9
                val scaleX = (595f / 2) / image.plainWidth * 100
                val scaleY = (842f / 2) / image.plainHeight * 100
                image.scalePercent(scaleX, scaleY)
                val absoluteX = image.scaledWidth * x
                val absoluteY = image.scaledHeight * (1 - (y % 2))
                image.setAbsolutePosition(absoluteX , absoluteY)
                document.add(image)
            }
            document.close()

            return file
        }
    }
}