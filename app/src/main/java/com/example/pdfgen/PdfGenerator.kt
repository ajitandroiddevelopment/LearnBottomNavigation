package com.example.pdfgen

// In com/yourcompany/yourproject/utils/PdfGenerator.kt

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.pdfgen.R // Import your R class
import com.example.pdfgen.data.ReceiptData
import java.io.OutputStream
// In com/yourcompany/yourproject/utils/PdfGenerator.kt

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

import androidx.core.content.ContextCompat


object PdfGenerator {

    // --- NEW HELPER FUNCTION ---
    // This function reliably converts any drawable resource into a Bitmap.
    private fun getBitmapFromImage(context: Context, drawableId: Int): Bitmap? {
        // Get the drawable from the resources.
        val drawable: Drawable? = ContextCompat.getDrawable(context, drawableId)

        if (drawable is BitmapDrawable) {
            // If the drawable is already a BitmapDrawable, we can simply get the bitmap.
            return drawable.bitmap
        }

        // If the drawable is a VectorDrawable or other type, we need to draw it to a new Bitmap.
        if (drawable != null) {
            // Create a new Bitmap with the same dimensions as the drawable.
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            // Create a Canvas to draw on the new Bitmap.
            val canvas = Canvas(bitmap)
            // Set the bounds for the drawable.
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            // Draw the drawable onto the canvas.
            drawable.draw(canvas)
            return bitmap
        }
        return null
    }

    fun generatePdf(context: Context, data: ReceiptData): Uri? {
        val pageWidth = 595
        val pageHeight = 842
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // We pass the canvas to the drawing function.
        drawReceiptContent(context, canvas, data)

        pdfDocument.finishPage(page)

        val fileName = "FeeReceipt_${data.registrationNo}.pdf"
        var outputStream: OutputStream? = null
        var pdfUri: Uri? = null

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/FeeReceipts")
                }
            }
            val contentResolver = context.contentResolver
            pdfUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            pdfUri?.let {
                outputStream = contentResolver.openOutputStream(it)
                pdfDocument.writeTo(outputStream)
                Toast.makeText(context, "PDF Generated Successfully!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
            pdfUri = null
        } finally {
            outputStream?.close()
            pdfDocument.close()
        }

        return pdfUri
    }

    private fun drawReceiptContent(context: Context, canvas: Canvas, data: ReceiptData) {
        // --- Define Paint objects for different text styles ---
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 26f
            textAlign = Paint.Align.CENTER
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 16f
            textAlign = Paint.Align.CENTER
        }
        val labelPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 14f
        }
        val valuePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 14f
        }
        val totalLabelPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
        }
        val totalValuePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
            textAlign = Paint.Align.RIGHT
        }
        val linePaint = Paint().apply {
            strokeWidth = 2f
        }

        val pageWidth = canvas.width
        var yPos = 80f

        // --- Draw College Logo and Name ---
        // --- MODIFIED LINE ---
        // Use our new helper function to safely get the bitmap.
        val logoBitmap = getBitmapFromImage(context, R.drawable.ic_launcher_foreground)
        if (logoBitmap != null) {
            // Scale the bitmap to a fixed size, e.g., 60x60
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 60, 60, false)
            canvas.drawBitmap(scaledLogo, 40f, yPos - 40, null)
        }

        canvas.drawText("Metropolis Institute of Technology", (pageWidth / 2f) + 40, yPos, titlePaint)

        yPos += 60
        canvas.drawLine(40f, yPos, pageWidth - 40f, yPos, linePaint)
        yPos += 30

        canvas.drawText("OFFICIAL FEE RECEIPT", pageWidth / 2f, yPos, headerPaint)
        yPos += 40

        canvas.drawText("Receipt Date:", 60f, yPos, labelPaint)
        canvas.drawText(data.date, 200f, yPos, valuePaint)
        yPos += 30

        canvas.drawText("Student Name:", 60f, yPos, labelPaint)
        canvas.drawText(data.studentName, 200f, yPos, valuePaint)
        yPos += 30

        canvas.drawText("Registration No:", 60f, yPos, labelPaint)
        canvas.drawText(data.registrationNo, 200f, yPos, valuePaint)
        yPos += 60

        canvas.drawLine(40f, yPos, pageWidth - 40f, yPos, linePaint)
        yPos += 40

        canvas.drawText("Total Amount Paid", 60f, yPos, totalLabelPaint)
        canvas.drawText("₹ ${data.feeAmount}", pageWidth - 60f, yPos, totalValuePaint)
        yPos += 80

        canvas.drawText("Authorized Signatory", pageWidth - 140f, yPos, labelPaint)
    }
}