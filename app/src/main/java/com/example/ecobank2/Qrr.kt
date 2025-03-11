package com.example.ecobank2


import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class Qrr: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrr)

        val qrImage = findViewById<ImageView>(R.id.qrImage)
        val userIdText = findViewById<TextView>(R.id.userIdText)
        val generateBtn = findViewById<Button>(R.id.generateBtn)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        user?.let {
            val userId = it.uid
            userIdText.text = "User ID: $userId"

            generateQRCode(userId, qrImage)

            generateBtn.setOnClickListener {
                generateQRCode(userId, qrImage)
            }
        }
    }

    private fun generateQRCode(userId: String, imageView: ImageView) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(userId, BarcodeFormat.QR_CODE, 400, 400)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
