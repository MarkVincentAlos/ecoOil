package com.example.ecobank2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

fun saveTransaction(pointsEarned: Int, amount: Double) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val transactionData = hashMapOf(
        "userId" to userId,
        "pointsEarned" to pointsEarned,
        "amount" to amount,
        "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp() // Use server timestamp
    )

    FirebaseFirestore.getInstance()
        .collection("transactions")
        .add(transactionData)
        .addOnSuccessListener {
            Log.d("Firestore", "Transaction added successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error adding transaction", e)
        }
}
