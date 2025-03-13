package com.example.ecobank2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun addTransaction(userId: String, amountSpent: Double) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)
    val transactionRef = db.collection("transactions").document()

    db.runTransaction { transaction ->
        val userSnapshot = transaction.get(userRef)
        val currentPoints = userSnapshot.getLong("points") ?: 0
        val pointsEarned = (amountSpent * 0.1).toInt()

        transaction.update(userRef, "points", currentPoints + pointsEarned)

        val newTransaction = hashMapOf(
            "userId" to userId,
            "amount" to amountSpent,
            "pointsEarned" to pointsEarned,
            "timestamp" to System.currentTimeMillis()
        )
        transaction.set(transactionRef, newTransaction)

        null
    }.addOnSuccessListener {
        Log.d("Firestore", "Transaction successful: Points added!")
    }.addOnFailureListener { e ->
        Log.e("Firestore", "Transaction failed: ", e)
    }
}
