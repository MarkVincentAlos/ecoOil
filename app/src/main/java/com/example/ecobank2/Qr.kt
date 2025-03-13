package com.example.ecobank2

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class Qrz : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrImage = view.findViewById<ImageView>(R.id.qrImage)
        val userIdText = view.findViewById<TextView>(R.id.userIdText)
        val generateBtn = view.findViewById<Button>(R.id.generateBtn)


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
